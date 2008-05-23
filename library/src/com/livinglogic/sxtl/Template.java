package com.livinglogic.sxtl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

public class Template
{
	public static class Location
	{
		public String source;
		public String type;
		public int starttag;
		public int endtag;
		public int startcode;
		public int endcode;

		public Location(String source, String type, int starttag, int endtag, int startcode, int endcode)
		{
			this.source = source;
			this.type = type;
			this.starttag = starttag;
			this.endtag = endtag;
			this.startcode = startcode;
			this.endcode = endcode;
		}
	}
	
	public static class Opcode
	{
		public String name;
		public int r1;
		public int r2;
		public int r3;
		public int r4;
		public int r5;
		public String arg;
		public Location location;
		public int jump;
		
		public Opcode(String name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
		{
			this.name = name;
			this.r1 = r1;
			this.r2 = r2;
			this.r3 = r3;
			this.r4 = r4;
			this.r5 = r5;
			this.arg = arg;
			this.location = location;
			this.jump = -1;
		}
	}

	public static final String SXTL_HEADER = "sxtl";

	public static final String SXTL_VERSION = "1";
	
	protected String source;

	protected List opcodes;

	private Template()
	{
		this.source = null;
		this.opcodes = new LinkedList();
	}

	protected static int readintInternal(Reader reader, char terminator) throws IOException
	{
		int retVal = 0;
		boolean terminatorFound = false;
		int readInt = reader.read();
		char charValue;
		int intValue;
		while ((-1 < readInt) && !terminatorFound)
		{
			charValue = (char)readInt;
			intValue = Character.digit(charValue, 10);
			if (-1 < intValue)
			{
				retVal = retVal * 10 + intValue;
			}
			else if (charValue == terminator)
			{
				terminatorFound = true;
			}
			else if (Character.toLowerCase(charValue) == terminator)
			{
				terminatorFound = true;
				retVal = -1;
			}
			else
			{
				throw new RuntimeException("Invalid terminator, expected " + terminator + ", got " + charValue);
			}
			if (!terminatorFound)
			{
				readInt = reader.read();
			}
		}
		return retVal;
	}

	protected static int readint(Reader reader, char terminator) throws IOException
	{
		int retVal = readintInternal(reader, terminator);
		if (0 > retVal)
		{
			throw new RuntimeException("Invalid integer read!");
		}
		return retVal;
	}

	protected static String readstr(Reader reader, char terminator) throws IOException
	{
		String retVal = null;
		int stringLength = readintInternal(reader, terminator);
		if (-1 < stringLength)
		{
			char[] retValChars = new char[stringLength];
			int readCharSize = reader.read(retValChars);
			if (readCharSize != stringLength)
			{
				throw new RuntimeException("Short read!");
			}
			retVal = new String(retValChars);
		}
		return retVal;
	}

	protected static int readspec(Reader reader) throws IOException
	{
		int retVal;
		int readInt = reader.read();
		char charValue;
		if (-1 < readInt)
		{
			charValue = (char)readInt;
			if ('-' == charValue)
			{
				retVal = -1;
			}
			else
			{
				retVal = Character.digit(charValue, 10);
				if (0 > retVal)
				{
					throw new RuntimeException("Invalid register spec " + charValue);
				}
			}
		}
		else
		{
			throw new RuntimeException("Short read!");
		}
		return retVal;
	}

	protected static void readcr(Reader reader) throws IOException
	{
		int readInt = reader.read();
		if (-1 < readInt)
		{
			char charValue = (char)readInt;
			if ('\n' != charValue)
			{
				throw new RuntimeException("Invalid linefeed " + charValue);
			}
		}
		else
		{
			throw new RuntimeException("Short read!");
		}
	}

	public static Template frombin(Reader reader) throws IOException
	{
		Template retVal = new Template();
		BufferedReader bufferedReader = new BufferedReader(reader);
		String header = bufferedReader.readLine();
		if (!SXTL_HEADER.equals(header))
		{
			throw new RuntimeException("Invalid header, expected " + SXTL_HEADER + ", got " + header);
		}
		String version = bufferedReader.readLine();
		if (!SXTL_VERSION.equals(version))
		{
			throw new RuntimeException("Invalid version, expected " + SXTL_VERSION + ", got " + version);
		}
		retVal.source = readstr(bufferedReader, 's');
		readcr(bufferedReader);
		int count = readint(bufferedReader, '#');
		readcr(bufferedReader);
		for (int i = 0; i < count; i++)
		{
			int r1 = readspec(bufferedReader);
			int r2 = readspec(bufferedReader);
			int r3 = readspec(bufferedReader);
			int r4 = readspec(bufferedReader);
			int r5 = readspec(bufferedReader);
			String code = readstr(bufferedReader, 'c');
			String arg = readstr(bufferedReader, 'a');
			Location location = null;
			int readInt = bufferedReader.read();
			if (-1 < readInt)
			{
				char charValue = (char)readInt;
				if ('^' == charValue)
				{
					if (null == location)
					{
						throw new RuntimeException("No previous location!");
					}
				}
				else if ('*' == charValue)
				{
					location = new Location(retVal.source, readstr(bufferedReader, 't'),
						readint(bufferedReader, '<'), readint(bufferedReader, '>'),
						readint(bufferedReader, '['), readint(bufferedReader, ']'));
				}
				else
				{
					throw new RuntimeException("Invalid location spec " + charValue);
				}
			}
			else
			{
				throw new RuntimeException("Short read!");
			}
			retVal.opcodes.add(new Opcode(code, r1, r2, r3, r4, r5, arg, location));
		}
		return retVal;
	}
}

