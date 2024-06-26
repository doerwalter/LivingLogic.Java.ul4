/*
** Copyright 2012-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Stack;

import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.MethodDescriptor;
import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.UL4GetAttr;
import com.livinglogic.ul4.UL4Dir;
import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.UL4Instance;
import com.livinglogic.ul4.AbstractInstanceType;
import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.MonthDelta;
import com.livinglogic.ul4.TimeDelta;
import com.livinglogic.ul4.Slice;
import com.livinglogic.ul4.FunctionRepr;
import com.livinglogic.ul4.BoundMethod;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.BoundArguments;
import com.livinglogic.ul4.AttributeException;
import com.livinglogic.ul4.ArgumentTypeMismatchException;


/**
A {@code Decoder} object wraps a {@code Reader} object and can read any object
in the UL4ON serialization format from this {@code Reader}.
**/
public class Decoder implements Iterable<Object>, UL4Instance, UL4Repr, UL4Dir
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getModuleName()
		{
			return "ul4on";
		}

		@Override
		public String getNameUL4()
		{
			return "Decoder";
		}

		@Override
		public String getDoc()
		{
			return "An UL4ON decoder";
		}

		@Override
		public Object create(EvaluationContext context, BoundArguments arguments)
		{
			return new Decoder();
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof Decoder;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	/**
	The {@code Reader} instance from where serialized objects currently will
	be read.
	Set temporarily during calls to {@code load}, so that the argument doesn't
	have to be passed around.
	**/
	private Reader reader = null;

	/**
	The current position in the UL4ON stream
	**/
	private int position = 0;

	/**
	The next character to be read by {@code nextChar}
	**/
	private int bufferedChar = -1;

	/**
	The list of objects that have been read so far from {@code reader} and
	that must be available for backreferences.
	**/
	private List<Object> backReferences = new ArrayList<Object>();

	/**
	Stores persistent objects (i.e. those whose
	{@link UL4ONSerializable#getUL$ONID} returns a non-{@code null} string).
	**/
	private Map<String, Map<String, UL4ONSerializable>> persistentObjects = new HashMap<String, Map<String, UL4ONSerializable>>();

	/**
	Custom type registry. Any type name not found in this registry will be
	looked up in the globals registry {@link Utils#registry}
	**/
	private Map<String, ObjectFactory> registry = null;

	/**
	Stack of types (used for error reporting).
	**/
	private Stack<String> stack = new Stack<String>();

	/**
	Create an {@code Decoder} object for reading serialized UL4ON dumps.
	**/
	public Decoder()
	{
	}

	/**
	Create an {@code Decoder} object for reading serialized UL4ON dumps.
	@param registry custom type registry.
	**/
	public Decoder(Map<String, ObjectFactory> registry)
	{
		this.registry = registry;
	}

	/**
	Return the list of objects the decoder has remembered for backreferences.

	Normally the user doesn't need this info. This is only useful to recreate
	the exact state of a {@code Decoder}.

	Never modify this list.

	@return the list of backreferences.
	**/
	public List<Object> getBackReferences()
	{
		return backReferences;
	}

	/**
	Reads a object in the UL4ON dump from the reader and return the deserialized object.
	@param reader the {@code Reader} from which the UL4ON dump will be read.
	@return the object read form the dump.
	@throws IOException if reading from the any underying {@link java.io.Reader} fails
	**/
	public Object load(Reader reader) throws IOException
	{
		this.reader = reader;
		position = 0;
		bufferedChar = -1;
		Object result = load();
		this.reader = null;
		return result;
	}

	/**
	Reads a object in the UL4ON dump from a string and return the deserialized object.
	@param dump the UL4ON dump.
	@return the object read from the dump
	**/
	public Object loads(String dump)
	{
		try (StringReader reader = new StringReader(dump))
		{
			return load(reader);
		}
		catch (IOException exc)
		{
			// can't happen anyway
			throw new RuntimeException(exc);
		}
	}

	/**
	Reads a object in the UL4ON dump from the reader and returns it.
	This is called by implementations of {@link UL4ONSerializable}, but should
	not be called from outside, as {@code reader} may not be set in this case.

	@return the object read from the stream
	@throws IOException if reading from the any underying {@link java.io.Reader} fails
	**/
	public Object load() throws IOException
	{
		char typecode = nextChar();

		if (typecode == '^')
		{
			int position = (Integer)readInt();
			return backReferences.get(position);
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
				throw new DecoderException(position, path(), "expected 'T' or 'F', got " + charRepr(data));
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
			String result = Utils.parseUL4StringFromReader(reader).intern();
			if (typecode == 'S')
				loading(result);
			return result;
		}
		else if (typecode == 'c' || typecode == 'C')
		{
			int oldpos = -1;
			if (typecode == 'C')
				oldpos = beginFakeLoading();

			pushType("color");
			try
			{
				int r = (Integer)load();
				int g = (Integer)load();
				int b = (Integer)load();
				int a = (Integer)load();
				Color result = new Color(r, g, b, a);

				if (typecode == 'C')
					endFakeLoading(oldpos, result);
				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'z' || typecode == 'Z')
		{
			int oldpos = -1;
			if (typecode == 'Z')
				oldpos = beginFakeLoading();

			pushType("datetime");
			try
			{
				int year = (Integer)load();
				int month = (Integer)load();
				int day = (Integer)load();
				int hour = (Integer)load();
				int minute = (Integer)load();
				int second = (Integer)load();
				int microsecond = (Integer)load();
				LocalDateTime result = LocalDateTime.of(year, month, day, hour, minute, second, 1000*microsecond);

				if (typecode == 'Z')
					endFakeLoading(oldpos, result);

				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'x' || typecode == 'X')
		{
			int oldpos = -1;
			if (typecode == 'X')
				oldpos = beginFakeLoading();

			pushType("date");
			try
			{
				int year = (Integer)load();
				int month = (Integer)load();
				int day = (Integer)load();
				LocalDate result = LocalDate.of(year, month, day);

				if (typecode == 'X')
					endFakeLoading(oldpos, result);

				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 't' || typecode == 'T')
		{
			int oldpos = -1;
			if (typecode == 'T')
				oldpos = beginFakeLoading();

			pushType("timedelta");
			try
			{
				int days = (Integer)load();
				int seconds = (Integer)load();
				int microseconds = (Integer)load();
				TimeDelta result = new TimeDelta(days, seconds, microseconds);

				if (typecode == 'T')
					endFakeLoading(oldpos, result);
				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'm' || typecode == 'M')
		{
			int oldpos = -1;
			if (typecode == 'M')
				oldpos = beginFakeLoading();

			pushType("monthdelta");
			try
			{
				int months = (Integer)load();
				MonthDelta result = new MonthDelta(months);

				if (typecode == 'M')
					endFakeLoading(oldpos, result);
				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'l' || typecode == 'L')
		{
			List result = new ArrayList();

			if (typecode == 'L')
				loading(result);

			pushType("list");
			try
			{
				while (true)
				{
					typecode = nextChar();
					if (typecode == ']')
						return result;
					else
					{
						pushbackChar(typecode);
						result.add(load());
					}
				}
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'd' || typecode == 'D' || typecode == 'e' || typecode == 'E')
		{
			Map result = (typecode == 'e' || typecode == 'E') ? new LinkedHashMap() : new HashMap();

			if (typecode == 'D' || typecode == 'E')
				loading(result);

			pushType("list");
			try
			{
				while (true)
				{
					typecode = nextChar();
					if (typecode == '}')
						return result;
					else
					{
						pushbackChar(typecode);
						Object key = load();
						Object value = load();

						result.put(key, value);
					}
				}
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'y' || typecode == 'Y')
		{
			Set result = new HashSet();

			if (typecode == 'Y')
				loading(result);

			pushType("set");
			try
			{
				while (true)
				{
					typecode = nextChar();
					if (typecode == '}')
						return result;
					else
					{
						pushbackChar(typecode);
						Object item = load();
						result.add(item);
					}
				}
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'r' || typecode == 'R')
		{
			int oldpos = -1;
			if (typecode == 'R')
				oldpos = beginFakeLoading();

			pushType("set");
			Object start;
			Object stop;
			try
			{
				start = load();
				stop = load();
			}
			finally
			{
				popType();
			}
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

			String name = (String)load();

			UL4ONSerializable result = createObject(name, null);
			if (result == null)
				throw new DecoderException(position, path(), com.livinglogic.ul4.Utils.formatMessage("can't load object of type {!r}", name));

			if (typecode == 'O')
				endFakeLoading(oldpos, result);

			pushType(name);
			try
			{
				result.loadUL4ON(this);

				int nextTypecode = nextChar();

				if (nextTypecode != ')')
					throw new DecoderException(position, path(), "object terminator ')' expected, got " + charRepr(nextTypecode));

				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'p' || typecode == 'P')
		{
			int oldpos = -1;
			if (typecode == 'P')
				oldpos = beginFakeLoading();

			String name = (String)load();
			String id = (String)load();

			UL4ONSerializable result = createObject(name, id);
			if (result == null)
			{
				throw new DecoderException(position, path(), com.livinglogic.ul4.Utils.formatMessage("can't load object of type {!r} with id {!r}", name, id));
			}

			if (typecode == 'P')
				endFakeLoading(oldpos, result);

			pushType(name);
			try
			{
				result.loadUL4ON(this);

				int nextTypecode = nextChar();

				if (nextTypecode != ')')
					throw new DecoderException(position, path(), "object terminator ')' expected, got " + charRepr(nextTypecode));

				return result;
			}
			finally
			{
				popType();
			}
		}
		else
			throw new DecoderException(position, path(), "unknown typecode " + charRepr(typecode));
	}

	/**
	Clear the internal cache for backreferences.
	However the cache for persistent objects will not be cleared.
	**/
	public void reset()
	{
		reader = null;
		position = 0;
		bufferedChar = -1;
		backReferences = new ArrayList<Object>();
		stack = new Stack<String>();
	}

	/**
	Return an iterator that reads the content of an object until the "object terminator" {@code )} is hit.

	This object terminator will not be read from the input stream.

	Also note, that the iterator should always be exhausted when it is read, otherwise the stream will be
	in an undefined state.
	**/
	public Iterator<Object> iterator()
	{
		return new ObjectContentIterator();
	}

	/**
	Return the persistent object with the type {@code type} and the id {@code id},
	or {@code null}, when the decoder hasn't encountered that object yet.

	@param type the UL4ON type name of the object to look up.
	@param id the UL4ON id of the object to look up.
	@return the object with the passed in type and id (or {@code null}).
	**/
	public UL4ONSerializable getPersistentObject(String type, String id)
	{
		Map<String, UL4ONSerializable> objects = persistentObjects.get(type);
		if (objects == null)
			return null;
		return objects.get(id);
	}

	/**
	Store the persistent object {@code object} in the persistent object
	cache. This way, when a persistent object with the same type and id as
	{@code object} is encountered again while deserializing an UL4ON stream,
	{@code object} will be reused and updated, instead of creating a new
	object with this type and id.

	@param object the object to be stored in the persistent object cache.
	**/
	public void storePersistentObject(UL4ONSerializable object)
	{
		String type = object.getUL4ONName();
		Map<String, UL4ONSerializable> objects = persistentObjects.get(type);
		if (objects == null)
		{
			objects = new HashMap<String, UL4ONSerializable>();
			persistentObjects.put(type, objects);
		}
		objects.put(object.getUL4ONID(), object);
	}

	/**
	Remove the persistent object {@code object} from the persistent object
	cache. This might be useful if an object gets created in a database
	transaction that gets rolled back later.

	@param object the object to be removed from the persistent object cache.
	**/
	public void forgetPersistentObject(UL4ONSerializable object)
	{
		String type = object.getUL4ONName();
		Map<String, UL4ONSerializable> objects = persistentObjects.get(type);
		if (objects != null)
			objects.remove(object.getUL4ONID());
	}

	/**
	Return an iterator over all objects in the persistent object cache.

	@return the iterator
	**/
	public Iterator<UL4ONSerializable> allPersistentObjects()
	{
		return new PersistentObjectIterator();
	}

	/**
	Record {@code obj} in the list of backreferences.
	**/
	private void loading(Object obj)
	{
		backReferences.add(obj);
	}

	/**
	For loading custom object or immutable objects that have attributes we have a problem:
	We have to record the object we're loading *now*, so that it is available for backreferences.
	However until we've read the UL4ON name of the class (for custom object) or the attributes
	of the object (for immutable objects with attributes), we can't create the object.
	So we push {@code null} to the backreference list for now and put the right object in this spot,
	once we've created it (via {@code endFakeLoading}). This shouldn't lead to problems,
	because during the time the backreference is wrong, only the class name is read,
	so our object won't be referenced. For immutable objects the attributes normally
	don't reference the object itself.
	*/
	private int beginFakeLoading()
	{
		int oldpos = backReferences.size();
		loading(null);
		return oldpos;
	}

	/**
	Fixes backreferences in object list
	**/
	private void endFakeLoading(int oldpos, Object value)
	{
		backReferences.set(oldpos, value);
	}

	private int readChar() throws IOException
	{
		int c = reader.read();
		++position;
		return c;
	}

	/**
	Read the next not-whitespace character
	**/
	private char nextChar() throws IOException
	{
		if (bufferedChar >= 0)
		{
			char result = (char)bufferedChar;
			bufferedChar = -1;
			return result;
		}
		while (true)
		{
			int c = readChar();
			if (c == -1)
				throw new DecoderException(position, path(), "unexpected EOF");

			if (!Character.isWhitespace(c))
				return (char)c;
		}
	}

	private void pushbackChar(char c)
	{
		bufferedChar = c;
	}

	private String charRepr(int c)
	{
		if (c < 0)
			return "EOF";
		else
			return FunctionRepr.call(Character.toString((char)c));
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

	private UL4ONSerializable createObject(String type, String id)
	{
		UL4ONSerializable result = null;

		if (id != null)
			result = getPersistentObject(type, id);

		if (result != null)
			return result;

		ObjectFactory factory = null;

		if (registry != null)
			factory = registry.get(type);

		if (factory == null)
			factory = Utils.registry.get(type);

		if (factory != null)
		{
			result = factory.create(id);
			if (result != null && id != null)
				storePersistentObject(result);
		}
		return result;
	}

	private void pushType(String type)
	{
		stack.push(type);
	}

	private void popType()
	{
		stack.pop();
	}

	private String path()
	{
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < stack.size(); ++i)
		{
			if (i > 0)
				buffer.append("/");
			buffer.append(stack.get(i));
		}
		return buffer.toString();
	}

	private class ObjectContentIterator implements Iterator
	{
		private char bufferedChar;

		ObjectContentIterator()
		{
			readTypeCode();
		}

		public boolean hasNext()
		{
			return bufferedChar != ')';
		}

		public Object next()
		{
			if (bufferedChar == ')')
				return null;
			else
			{
				Object item = null;
				try
				{
					item = load();
				}
				catch (RuntimeException ex)
				{
					throw ex;
				}
				catch (Exception ex)
				{
					throw new RuntimeException(ex);
				}
				readTypeCode();
				return item;
			}
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		private void readTypeCode()
		{
			try
			{
				bufferedChar = nextChar();
			}
			catch (RuntimeException ex)
			{
				throw ex;
			}
			catch (Exception ex)
			{
				throw new RuntimeException(ex);
			}
			pushbackChar(bufferedChar);
		}
	}

	private class PersistentObjectIterator implements Iterator<UL4ONSerializable>
	{
		/**
		Iterator over the outer map
		**/
		private Iterator<Map<String, UL4ONSerializable>> outerIterator;

		/**
		Iterator over the inner map
		If the {@code PersistentObjectIterator} is exhausted, this will be {@code null}.
		**/
		private Iterator<UL4ONSerializable> innerIterator;

		PersistentObjectIterator()
		{
			outerIterator = persistentObjects.values().iterator();
			innerIterator = outerIterator.hasNext() ? outerIterator.next().values().iterator() : null;
		}

		public boolean hasNext()
		{
			skip();
			return innerIterator != null;
		}

		public UL4ONSerializable next()
		{
			UL4ONSerializable result = innerIterator.next();
			skip();
			return result;
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		/**
		If {@code this} isn't exhausted, but the {@code innerIterator} is,
		move the {@code outerIterator} until its corresponding
		{@code innerIterator} can give as values. If no such
		{@code innerIterator} exists, the iteration is finished and we
		set {@code innerIterator} to {@code null} to mark that fact.
		**/
		private void skip()
		{
			if (innerIterator != null)
			{
				if (!innerIterator.hasNext())
				{
					while (outerIterator.hasNext())
					{
						innerIterator = outerIterator.next().values().iterator();
						if (innerIterator.hasNext())
							return;
					}
					innerIterator = null;
				}
			}
		}
	}

	@Override
	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter
			.append("< ")
			.append(getClass().getName())
			.append(">")
		;
	}

	private static final Signature signatureLoadS = new Signature().addPositionalOnly("dump");
	private static final MethodDescriptor<Decoder> methodLoadS = new MethodDescriptor<Decoder>(type, "loads", signatureLoadS);
	private static final MethodDescriptor<Decoder> methodReset = new MethodDescriptor<Decoder>(type, "reset", Signature.noParameters);

	protected static Set<String> attributes = Set.of("loads");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "loads":
				return methodLoadS.bindMethod(this);
			default:
				return UL4Instance.super.getAttrUL4(context, key);
		}
	}

	@Override
	public Object callAttrUL4(EvaluationContext context, String key, List<Object> args, Map<String, Object> kwargs)
	{
		switch (key)
		{
			case "loads":
				BoundArguments boundLoadSArgs = methodLoadS.bindArguments(args, kwargs);
				String arg = boundLoadSArgs.getString(0);
				return loads(arg);
			case "reset":
				BoundArguments boundArgs = methodReset.bindArguments(args, kwargs);
				reset();
				return null;
			default:
				return UL4Instance.super.callAttrUL4(context, key, args, kwargs);
		}
	}
}
