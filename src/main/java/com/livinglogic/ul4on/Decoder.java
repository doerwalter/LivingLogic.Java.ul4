/*
** Copyright 2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.livinglogic.ul4.Color;

/**
 * Utility class for reading and writing the UL4ON object serialization format.
 *
 * The UL4ON object serialization format is a simple (text-based) serialization format
 * the supports all objects supported by UL4, i.e. it supports the same type of objects
 * as JSON does (plus colors, dates and templates)
 *
 * @author W. Dörwald, A. Gaßner
 */
public class Decoder
{
	private Reader reader = null;
	private List<Object> objects = new ArrayList<Object>();
	private Map<Object, Object> keys = new HashMap<Object, Object>();

	public Decoder(Reader reader)
	{
		this.reader = reader;
	}

	/**
	 * Reads a object in the UL4ON object serialization from the reader and returns it.
	 * @returns the object read from the stream
	 * @throws IOException if reading from the stream fails
	 */
	public Object load() throws IOException
	{
		return load(-2);
	}
	
	private void loading(Object obj)
	{
		objects.add(obj);
	}

	private Object load(int typecode) throws IOException
	{
		if (typecode == -2)
			typecode = reader.read();
		
		if (typecode == '^')
		{
			int position = readInt();
			return objects.get(position);
		}
		else if (typecode == 'n' || typecode == 'N')
		{
			if (typecode == 'N')
				loading(null);
			return null;
		}
		else if (typecode == 'b' || typecode == 'B')
		{
			int data = reader.read();
			Boolean value;
			if (data == 'T')
				value = true;
			else if (data == 'F')
				value = false;
			else
				throw new RuntimeException("broken stream: expected 'T' or 'F', got '\\u" + Integer.toHexString(data) + "'");
			if (typecode == 'B')
				loading(value);
			return value;
		}
		else if (typecode == 'i' || typecode == 'I')
		{
			int value = readInt();
			if (typecode == 'I')
				loading(value);
			return value;
		}
		else if (typecode == 'f' || typecode == 'F')
		{
			double value = readFloat();
			if (typecode == 'F')
				loading(value);
			return value;
		}
		else if (typecode == 's' || typecode == 'S')
		{
			int count = readInt();
			char[] chars = new char[count];
			reader.read(chars);
			String value = new String(chars);
			if (typecode == 'S')
				loading(value);
			return value;
		}
		else if (typecode == 'c' || typecode == 'C')
		{
			char[] chars = new char[8];
			reader.read(chars);
			Color value = Color.fromdump(new String(chars));
			if (typecode == 'C')
				loading(value);
			return value;
		}
		else if (typecode == 't' || typecode == 'T')
		{
			char[] chars = new char[20];
			reader.read(chars);
			Date value;
			try
			{
				value = Utils.dateFormat.parse(new String(chars).substring(0, 17));
			}
			catch (ParseException e)
			{
				// can happen with broken data
				throw new RuntimeException(e);
			}
			if (typecode == 'T')
				loading(value);
			return value;
		}
		else if (typecode == 'l' || typecode == 'L')
		{
			List result = new ArrayList();

			if (typecode == 'L')
				loading(result);
			
			while (true)
			{
				typecode = reader.read();
				if (typecode == '.')
					return result;
				else
					result.add(load(typecode));
			}
		}
		else if (typecode == 'd' || typecode == 'D')
		{
			Map result = new HashMap();

			if (typecode == 'D')
				loading(result);

			while (true)
			{
				typecode = reader.read();
				if (typecode == '.')
					return result;
				else
				{
					Object key = load(typecode);
					Object value = load(-2);
					Object oldKey = keys.get(key);

					if (oldKey == null)
						keys.put(key, key);
					else
						key = oldKey;

					result.put(key, value);
				}
			}
		}
		else if (typecode == 'o' || typecode == 'O')
		{
			int oldpos = 1;
			if (typecode == 'O')
			{
				// We have a problem here:
				// We have to record the object we're loading *now*, so that it is available for backreferences.
				// However until we've read the UL4ON name of the class, we can't create the object.
				// So we push null to the backreference list for now and put the right object in this spot,
				// once we've created it (This shouldn't be a problem, because during the time the backreference
				// is wrong, only the class name is read, so our object won't be refenced).
				oldpos = objects.size();
				loading(null);
			}
			String name = (String)load(-2);

			ObjectFactory factory = Utils.registry.get(name);

			if (factory == null)
				throw new RuntimeException("can't load object of type " + name);
			UL4ONSerializable value = factory.create();
			// Fix object in backreference list
			if (oldpos != -1)
				objects.set(oldpos, value);
			value.loadUL4ON(this);
			return value;
		}
		else
			throw new RuntimeException("broken stream: unknown typecode '\\u" + Integer.toHexString(typecode) + "'");
	}
	
	private int readInt() throws IOException
	{
		StringBuffer buffer = new StringBuffer();
		
		while (true)
		{
			int c = reader.read();
			if (c == '|')
				return Integer.valueOf(buffer.toString());
			buffer.append((char)c);
		}
	}
	
	private double readFloat() throws IOException
	{
		StringBuffer buffer = new StringBuffer();

		while (true)
		{
			int c = reader.read();
			if (c == '|')
				return Double.valueOf(buffer.toString());
			buffer.append((char)c);
		}
	}
}
