package com.livinglogic.ul4on;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.livinglogic.ul4.Color;

/**
 * Yet another utils class.
 * @author W. Dörwald, A. Gaßner
 *
 */
public class Utils
{
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	public static void dump(Object data, Writer writer) throws IOException
	{
		if (data == null)
			writer.write("n");
		else if (data instanceof Boolean)
			writer.write(((Boolean)data).booleanValue() ? "bT" : "bF");
		else if (data instanceof Integer || data instanceof Byte || data instanceof Short || data instanceof BigInteger)
			writer.write("i" + data.toString() + "|");
		else if (data instanceof String)
		{
			writer.write("s" + ((String)data).length() + "|");
			writer.write((String)data);
		}
		else if (data instanceof Date)
			writer.write("d" + dateFormat.format((Date)data) + "000");
		else if (data instanceof Color)
			writer.write("c" + ((Color)data).dump());
		else if (data instanceof Collection)
		{
			writer.write("[");
			
			for (Object o: (Collection)data)
				dump(o, writer);
			
			writer.write("]");
		}
		else if (data instanceof Map)
		{
			writer.write("{");
			
			for (Map.Entry entry: ((Map<Object, Object>)data).entrySet())
			{
				dump(entry.getKey(), writer);
				dump(entry.getValue(), writer);
			}
			
			writer.write("}");
		}
		else
		{
			throw new RuntimeException("unknown type");
		}
	}
	
	public static String dumps(Object data)
	{
		StringWriter writer = new StringWriter();
		
		try
		{
			dump(data, writer);
		}
		catch (IOException ioe)
		{
			// can't happen with StringWriter
		}
		
		return writer.toString();
	}
	
	private static int readInt(Reader reader) throws IOException
	{
		int i = 0;
		
		while (true)
		{
			int c = reader.read();
			if (c == '|')
				return i;
			else if (c >= '0' && c <= '9')
				i = 10 * i + (c - '0');
			else
				throw new RuntimeException("broken stream: expected digit or '|', got '\\u" + Integer.toHexString(c) + "'");
		}
	}
	
	private static Object load(Reader reader, int typecode, Map keys) throws IOException
	{
		if (typecode == -2)
			typecode = reader.read();
		
		if (typecode == 'n')
			return null;
		else if (typecode == 'b')
		{
			int value = reader.read();
			if (value == 'T')
				return true;
			else if (value == 'F')
				return false;
			else
				throw new RuntimeException("broken stream: expected 'T' or 'F', got '\\u" + Integer.toHexString(value) + "'");
		}
		else if (typecode == 'i')
			return readInt(reader);
		else if (typecode == 's')
		{
			int count = readInt(reader);
			char[] chars = new char[count];
			reader.read(chars);
			return new String(chars);
		}
		else if (typecode == 'c')
		{
			char[] chars = new char[8];
			reader.read(chars);
			return Color.fromdump(new String(chars));
		}
		else if (typecode == 'd')
		{
			char[] chars = new char[20];
			reader.read(chars);
			try
			{
				return dateFormat.parse(new String(chars).substring(0, 17));
			}
			catch (ParseException e)
			{
				// can happen with broken data
				throw new RuntimeException(e);
			}
		}
		else if (typecode == '[')
		{
			List result = new ArrayList();
			
			while (true)
			{
				typecode = reader.read();
				if (typecode == ']')
					return result;
				else
					result.add(load(reader, typecode, keys));
			}
		}
		else if (typecode == '{')
		{
			Map result = new HashMap();
			
			while (true)
			{
				typecode = reader.read();
				if (typecode == '}')
					return result;
				else
				{
					Object key = load(reader, typecode, keys);
					Object value = load(reader, -2, keys);
					Object oldKey = keys.get(key);

					if (oldKey == null)
						keys.put(key, key);
					else
						key = oldKey;

					result.put(key, value);
				}
			}
		}
		else
			throw new RuntimeException("broken stream: unknown typecode '\\u" + Integer.toHexString(typecode) + "'");
	}
	
	public static Object load(Reader reader) throws IOException
	{
		return load(reader, -2, new HashMap());
	}
	
	public static Object load(String s)
	{
		try
		{
			return load(new StringReader(s));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// can't happen
			// keeps the compiler happy
			return null;
		}
	}
	
	public static Object load(Clob clob) throws IOException, SQLException
	{
		return load(clob.getCharacterStream());
	}
}
