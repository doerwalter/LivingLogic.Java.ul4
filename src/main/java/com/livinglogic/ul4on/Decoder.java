/*
** Copyright 2012-2017 by LivingLogic AG, Bayreuth/Germany
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.math.BigInteger;

import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.MonthDelta;
import com.livinglogic.ul4.TimeDelta;
import com.livinglogic.ul4.Slice;
import com.livinglogic.ul4.FunctionDate;
import com.livinglogic.ul4.FunctionRepr;

/**
 * A {@code Decoder} object wraps a {@code Reader} object and can read any object
 * in the UL4ON serialization format from this {@code Reader}.
 */
public class Decoder
{
	/**
	 * The {@code Reader} instance from where serialized objects will be read.
	 */
	private Reader reader = null;

	/**
	 * The current position in the UL4ON stream
	 */
	int position = 0;

	/**
	 * The list of objects that have been read so far from {@code reader} and
	 * that must be available for backreferences.
	 */
	private List<Object> objects = new ArrayList<Object>();

	/**
	 * A {@code Map} that maps string to strings of the same value. This is used
	 * to make sure that string keys in a map always use the same string objects.
	 */
	private Map<Object, Object> keys = new HashMap<Object, Object>();

	/**
	 * Create an {@code Decoder} object for reading serialized UL4ON output
	 * from the {@code Reader} {@code reader}.
	 */
	public Decoder(Reader reader)
	{
		this.reader = reader;
	}

	/**
	 * Reads a object in the UL4ON object serialization from the reader and returns it.
	 * @return the object read from the stream
	 * @throws IOException if reading from the stream fails
	 */
	public Object load() throws IOException
	{
		return load(-2);
	}

	/**
	 * Record {@code obj} in the list of backreferences.
	 */
	private void loading(Object obj)
	{
		objects.add(obj);
	}

	/**
	 * For loading custom object or immutable objects that have attributes we have a problem:
	 * We have to record the object we're loading *now*, so that it is available for backreferences.
	 * However until we've read the UL4ON name of the class (for custom object) or the attributes
	 * of the object (for immutable objects with attributes), we can't create the object.
	 * So we push {@code null} to the backreference list for now and put the right object in this spot,
	 * once we've created it (via {@code endFakeLoading}). This shouldn't lead to problems,
	 * because during the time the backreference is wrong, only the class name is read,
	 * so our object won't be referenced. For immutable objects the attributes normally
	 * don't reference the object itself.
	*/
	private int beginFakeLoading()
	{
		int oldpos = objects.size();
		loading(null);
		return oldpos;
	}

	/**
	 * Fixes backreferences in object list
	 */
	private void endFakeLoading(int oldpos, Object value)
	{
		objects.set(oldpos, value);
	}

	private int readChar() throws IOException
	{
		int c = reader.read();
		++position;
		return c;
	}

	/**
	 * Read the next not-whitespace character
	 */
	private char nextChar() throws IOException
	{
		while (true)
		{
			int c = readChar();
			if (c == -1)
				throw new DecoderException(position, "broken stream: unexpected EOF");

			if (!Character.isWhitespace(c))
				return (char)c;
		}
	}

	private String charRepr(int c)
	{
		if (c < 0)
			return "EOF";
		else
			return FunctionRepr.call(Character.toString((char)c));
	}

	/**
	 * Read an object from {@link #reader} and return it.
	 * @param typecode The typecode from a previous read or -2 if the typecode
	 *                 has to be read from the {@link #reader}.
	 */
	private Object load(int typecode) throws IOException
	{
		if (typecode == -2)
			typecode = nextChar();

		if (typecode == '^')
		{
			int position = (Integer)readInt();
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
			int data = readChar();
			Boolean result;
			if (data == 'T')
				result = true;
			else if (data == 'F')
				result = false;
			else
				throw new DecoderException(position, "broken stream: expected 'T' or 'F', got " + charRepr(data));
			if (typecode == 'B')
				loading(result);
			++position;
			return result;
		}
		else if (typecode == 'i' || typecode == 'I')
		{
			StringBuilder buffer = new StringBuilder();
			Object result = null;
			while (true)
			{
				int c = readChar();
				if (c == '-' || Character.isDigit(c))
					buffer.append((char)c);
				else
				{
					String string = buffer.toString();
					try
					{
						result = Integer.parseInt(string);
					}
					catch (NumberFormatException ex1)
					{
						try
						{
							result = Long.parseLong(string);
						}
						catch (NumberFormatException ex2)
						{
							result = new BigInteger(string);
						}
					}
					break;
				}
			}
			if (typecode == 'I')
				loading(result);
			return result;
		}
		else if (typecode == 'f' || typecode == 'F')
		{
			double result = readFloat();
			if (typecode == 'F')
				loading(result);
			return result;
		}
		else if (typecode == 's' || typecode == 'S')
		{
			String result = Utils.parseUL4StringFromReader(reader);
			if (typecode == 'S')
				loading(result);
			return result;
		}
		else if (typecode == 'c' || typecode == 'C')
		{
			int oldpos = -1;
			if (typecode == 'C')
				oldpos = beginFakeLoading();

			int r = (Integer)load(-2);
			int g = (Integer)load(-2);
			int b = (Integer)load(-2);
			int a = (Integer)load(-2);
			Color result = new Color(r, g, b, a);

			if (typecode == 'C')
				endFakeLoading(oldpos, result);
			return result;
		}
		else if (typecode == 'z' || typecode == 'Z')
		{
			int oldpos = -1;
			if (typecode == 'Z')
				oldpos = beginFakeLoading();

			int year = (Integer)load(-2);
			int month = (Integer)load(-2);
			int day = (Integer)load(-2);
			int hour = (Integer)load(-2);
			int minute = (Integer)load(-2);
			int second = (Integer)load(-2);
			int microsecond = (Integer)load(-2);
			Date result = FunctionDate.call(year, month, day, hour, minute, second, microsecond);

			if (typecode == 'Z')
				endFakeLoading(oldpos, result);

			return result;
		}
		else if (typecode == 't' || typecode == 'T')
		{
			int oldpos = -1;
			if (typecode == 'T')
				oldpos = beginFakeLoading();

			int days = (Integer)load(-2);
			int seconds = (Integer)load(-2);
			int microseconds = (Integer)load(-2);
			TimeDelta result = new TimeDelta(days, seconds, microseconds);

			if (typecode == 'T')
				endFakeLoading(oldpos, result);
			return result;
		}
		else if (typecode == 'm' || typecode == 'M')
		{
			int oldpos = -1;
			if (typecode == 'M')
				oldpos = beginFakeLoading();

			int months = (Integer)load(-2);
			MonthDelta result = new MonthDelta(months);

			if (typecode == 'M')
				endFakeLoading(oldpos, result);
			return result;
		}
		else if (typecode == 'l' || typecode == 'L')
		{
			List result = new ArrayList();

			if (typecode == 'L')
				loading(result);
			
			while (true)
			{
				typecode = nextChar();
				if (typecode == ']')
					return result;
				else
					result.add(load(typecode));
			}
		}
		else if (typecode == 'd' || typecode == 'D' || typecode == 'e' || typecode == 'E')
		{
			Map result = (typecode == 'e' || typecode == 'E') ? new LinkedHashMap() : new HashMap();

			if (typecode == 'D' || typecode == 'E')
				loading(result);

			while (true)
			{
				typecode = nextChar();
				if (typecode == '}')
					return result;
				else
				{
					Object key = load(typecode);
					Object value = load(-2);
					if (key instanceof String)
					{
						Object oldKey = keys.get(key);

						if (oldKey == null)
							keys.put(key, key);
						else
							key = oldKey;
					}

					result.put(key, value);
				}
			}
		}
		else if (typecode == 'y' || typecode == 'Y')
		{
			Set result = new HashSet();

			if (typecode == 'Y')
				loading(result);

			while (true)
			{
				typecode = nextChar();
				if (typecode == '}')
					return result;
				else
				{
					Object item = load(typecode);
					result.add(item);
				}
			}
		}
		else if (typecode == 'r' || typecode == 'R')
		{
			int oldpos = -1;
			if (typecode == 'R')
				oldpos = beginFakeLoading();

			Object start = load(-2);
			Object stop = load(-2);
			boolean hasStart = (start != null);
			boolean hasStop = (stop != null);
			int startIndex = hasStart ? com.livinglogic.ul4.Utils.toInt(start) : -1;
			int stopIndex = hasStop ? com.livinglogic.ul4.Utils.toInt(stop) : -1;
			Slice result = new Slice(hasStart, hasStop, startIndex, stopIndex);
			if (typecode == 'R')
				endFakeLoading(oldpos, result);
			return result;
		}
		else if (typecode == 'o' || typecode == 'O')
		{
			int oldpos = -1;
			if (typecode == 'O')
				oldpos = beginFakeLoading();

			String name = (String)load(-2);

			ObjectFactory factory = Utils.registry.get(name);

			if (factory == null)
				throw new DecoderException(position, "can't load object of type " + name);
			UL4ONSerializable result = factory.create();

			if (typecode == 'O')
				endFakeLoading(oldpos, result);

			result.loadUL4ON(this);

			int nextTypecode = nextChar();

			if (nextTypecode != ')')
				throw new DecoderException(position, "broken stream : object terminator ')' expected, got " + charRepr(nextTypecode));

			return result;
		}
		else
			throw new DecoderException(position, "broken stream: unknown typecode " + charRepr(typecode));
	}

	private int readInt() throws IOException
	{
		StringBuilder buffer = new StringBuilder();
		
		while (true)
		{
			int c = readChar();
			if (c == '-' || Character.isDigit(c))
				buffer.append((char)c);
			else
			{
				return Integer.parseInt(buffer.toString());
			}
		}
	}

	private double readFloat() throws IOException
	{
		StringBuilder buffer = new StringBuilder();

		while (true)
		{
			int c = readChar();
			if (c == '-' || c == '+' || Character.isDigit(c) || c == '.' || c == 'e' || c == 'E')
				buffer.append((char)c);
			else
				return Double.valueOf(buffer.toString());
		}
	}
}
