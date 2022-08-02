/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import static java.util.Arrays.asList;


public class BoundArguments
{
	protected String name;
	protected Signature signature;
	protected Object[] argumentsByPosition;
	protected Map<String, Object> argumentsByName;

	public BoundArguments(Signature signature, UL4Name object, List<Object> args, Map<String, Object> kwargs)
	{
		this(signature, object.getFullNameUL4(), args, kwargs);
	}

	public BoundArguments(Signature signature, String name, List<Object> args, Map<String, Object> kwargs)
	{
		this.name = name;
		this.signature = signature;
		if (signature == null)
		{
			// If we don't have a signature we only support keyword arguments
			if (args != null && args.size() > 0)
				throw new PositionalArgumentsNotSupportedException(name);

			argumentsByPosition = null;
			argumentsByName = kwargs != null ? new LinkedHashMap<String, Object>(kwargs) : new LinkedHashMap<String, Object>();
		}
		else
		{
			int i;
			int count = signature.count();

			ParameterDescription varPositionalParameter = signature.getVarPositional();
			ParameterDescription varKeywordParameter = signature.getVarKeyword();

			List<Object> varPositionalArguments = varPositionalParameter != null ? new ArrayList<Object>() : null;
			LinkedHashMap<String, Object> varKeywordArguments = varKeywordParameter != null ? new LinkedHashMap<String, Object>() : null;

			argumentsByPosition = new Object[count + (varPositionalParameter != null ? 1 : 0) + (varKeywordParameter != null ? 1 : 0)];
			argumentsByName = null; // will be created on demand
			boolean[] haveValue = new boolean[count];

			// Handle positional arguments
			if (args != null)
			{
				i = 0;
				for (Object argValue : args)
				{
					ParameterDescription param = signature.getParameterByPosition(i);
					if (param != null && param.isPositional())
					{
						// A parameter exists in this position and it can be specified positionally
						argumentsByPosition[i] = argValue;
						haveValue[i] = true;
					}
					else
					{
						// No positional parameter supported in this position, see if we have var positionals (i.e. an {@code *args} parameter)
						if (varPositionalArguments != null)
							// If we do, add it to the {@code *} parameter
							varPositionalArguments.add(argValue);
						else
							// else complain
							throw new TooManyArgumentsException(name, signature, args.size());
					}
					++i;
				}
			}

			// Handle keyword arguments
			if (kwargs != null)
			{
				for (Map.Entry<String, Object> kwarg : kwargs.entrySet())
				{
					String argName = kwarg.getKey();
					Object argValue = kwarg.getValue();

					ParameterDescription param = signature.getParameterByName(argName);
					if (param != null && param.isKeyword())
					{
						// A parameter exists with this name and it can be specified via keyword
						int position = param.getPosition();
						if (haveValue[position])
							throw new DuplicateArgumentException(name, param);
						else
						{
							argumentsByPosition[position] = argValue;
							haveValue[position] = true;
						}
					}
					else
					{
						// No keyword parameter supported with this name, see if we have var keywords (i.e. an {@code *kwargs} parameter)
						if (varKeywordArguments != null)
						{
							// If we do, add it to the {@code **} parameter (but only once)
							if (varKeywordArguments.containsKey(argName))
								throw new DuplicateArgumentException(name, argName);
							varKeywordArguments.put(argName, argValue);
						}
						else
							// else complain
							throw new UnsupportedArgumentNameException(name, argName);
					}
				}
			}

			// Fill in default values and check that every parameter has a value
			i = 0;
			for (ParameterDescription param : signature.getParametersByPosition())
			{
				if (!haveValue[i])
				{
					if (param.hasDefault())
					{
						argumentsByPosition[i] = param.getDefaultValue();
						haveValue[i] = true;
					}
					else
						throw new MissingArgumentException(name, param);
				}
				++i;
			}

			// Set variable parameters
			if (varPositionalArguments != null)
				argumentsByPosition[i++] = varPositionalArguments;
			if (varKeywordArguments != null)
				argumentsByPosition[i++] = varKeywordArguments;
		}
	}

	public Object[] byPosition()
	{
		return argumentsByPosition;
	}

	private void makeArgumentsByName()
	{
		if (argumentsByName == null)
		{
			argumentsByName = new LinkedHashMap<String, Object>(argumentsByPosition.length);

			int i = 0;
			for (ParameterDescription param : signature.getParametersByPosition())
				argumentsByName.put(param.getName(), argumentsByPosition[i++]);

			ParameterDescription varPositional = signature.getVarPositional();
			if (varPositional != null)
				argumentsByName.put(varPositional.getName(), argumentsByPosition[i++]);

			ParameterDescription varKeyword = signature.getVarKeyword();
			if (varKeyword != null)
				argumentsByName.put(varKeyword.getName(), argumentsByPosition[i++]);
		}
	}

	public Map<String, Object> byName()
	{
		makeArgumentsByName();
		return argumentsByName;
	}

	public Object get(int position)
	{
		return argumentsByPosition[position];
	}

	public Object get(String name)
	{
		makeArgumentsByName();
		return argumentsByName.get(name);
	}

	private ArgumentTypeMismatchException wrongArgumentType(String requiredType, Object argumentValue, int argumentPosition, String argumentName)
	{
		if (argumentPosition < 0 && argumentName != null && signature != null)
		{
			ParameterDescription parameterDescription = signature.getParameterByName(argumentName);
			if (parameterDescription != null)
				argumentPosition = parameterDescription.getPosition();
		}
		if (argumentName == null && argumentPosition >= 0 && signature != null)
		{
			ParameterDescription parameterDescription = signature.getParameterByPosition(argumentPosition);
			if (parameterDescription != null)
				argumentName = parameterDescription.getName();
		}

		if (argumentName != null)
		{
			if (argumentPosition >= 0)
			{
				return new ArgumentTypeMismatchException(
					"{}() argument {!r} at position {} must be {} but is {!t}!",
					name,
					argumentName,
					argumentPosition,
					requiredType,
					argumentValue
				);
			}
			else
			{
				return new ArgumentTypeMismatchException(
					"{}() argument {!r} must be {} but is {!t}!",
					name,
					argumentName,
					requiredType,
					argumentValue
				);
			}
		}
		else
		{
			if (argumentPosition >= 0)
			{
				return new ArgumentTypeMismatchException(
					"{}() argument at position {} must be {} but is {!t}!",
					name,
					argumentPosition,
					requiredType,
					argumentValue
				);
			}
			else
			{
				return new ArgumentTypeMismatchException(
					"{}() argument must be {} but is {!t}!",
					name,
					requiredType,
					argumentValue
				);
			}
		}
	}

	private boolean makeBool(Object value, int position, String name)
	{
		if (value instanceof Boolean)
			return ((Boolean)value).booleanValue();
		throw wrongArgumentType("<bool>", value, position, name);
	}

	private boolean makeBool(Object value, boolean nullValue, int position, String name)
	{
		if (value == null)
			return nullValue;
		return makeBool(value, position, name);
	}

	public boolean getBool(int position)
	{
		return makeBool(get(position), position, null);
	}

	public boolean getBool(String name)
	{
		makeArgumentsByName();
		return makeBool(argumentsByName.get(name), -1, name);
	}

	public boolean getBool(int position, boolean nullValue)
	{
		return makeBool(get(position), nullValue, position, null);
	}

	public boolean getBool(String name, boolean nullValue)
	{
		makeArgumentsByName();
		return makeBool(argumentsByName.get(name), nullValue, -1, name);
	}

	private int makeInt(Object value, int position, String name)
	{
		if (value instanceof Boolean)
			return ((Boolean)value).booleanValue() ? 1 : 0;
		else if (value instanceof Number)
			return ((Number)value).intValue();
		throw wrongArgumentType("<int>", value, position, name);
	}

	private int makeInt(Object value, int nullValue, int position, String name)
	{
		if (value == null)
			return nullValue;
		return makeInt(value, position, name);
	}

	public int getInt(int position)
	{
		return makeInt(get(position), position, null);
	}

	public int getInt(String name)
	{
		makeArgumentsByName();
		return makeInt(argumentsByName.get(name), -1, name);
	}

	public int getInt(int position, int nullValue)
	{
		return makeInt(get(position), nullValue, position, null);
	}

	public int getInt(String name, int nullValue)
	{
		makeArgumentsByName();
		return makeInt(argumentsByName.get(name), nullValue, -1, name);
	}

	private long makeLong(Object value, int position, String name)
	{
		if (value instanceof Boolean)
			return ((Boolean)value).booleanValue() ? 1 : 0;
		else if (value instanceof Number)
			return ((Number)value).longValue();
		throw wrongArgumentType("<int>", value, position, name);
	}

	private long makeLong(Object value, long nullValue, int position, String name)
	{
		if (value == null)
			return nullValue;
		return makeLong(value, position, name);
	}

	public long getLong(int position)
	{
		return makeLong(get(position), position, null);
	}

	public long getLong(String name)
	{
		makeArgumentsByName();
		return makeLong(argumentsByName.get(name), -1, name);
	}

	public long getLong(int position, long nullValue)
	{
		return makeLong(get(position), nullValue, position, null);
	}

	public long getLong(String name, long nullValue)
	{
		makeArgumentsByName();
		return makeLong(argumentsByName.get(name), nullValue, -1, name);
	}

	private double makeDouble(Object value, int position, String name)
	{
		if (value instanceof Boolean)
			return ((Boolean)value).booleanValue() ? 1.0d : 0.0d;
		else if (value instanceof Number)
			return ((Number)value).doubleValue();
		throw wrongArgumentType("<float>", value, position, name);
	}

	private double makeDouble(Object value, double nullValue, int position, String name)
	{
		if (value == null)
			return nullValue;
		return makeDouble(value, position, name);
	}

	public double getDouble(int position)
	{
		return makeDouble(get(position), position, null);
	}

	public double getDouble(String name)
	{
		makeArgumentsByName();
		return makeDouble(argumentsByName.get(name), -1, name);
	}

	public double getDouble(int position, double nullValue)
	{
		return makeDouble(get(position), nullValue, position, null);
	}

	public double getDouble(String name, double nullValue)
	{
		makeArgumentsByName();
		return makeDouble(argumentsByName.get(name), nullValue, -1, name);
	}

	private String makeString(Object value, int position, String name)
	{
		if (value instanceof String)
			return (String)value;
		throw wrongArgumentType("<str>", value, position, name);
	}

	private String makeString(Object value, String nullValue, int position, String name)
	{
		if (value == null)
			return nullValue;
		return makeString(value, position, name);
	}

	public String getString(int position)
	{
		return makeString(get(position), position, null);
	}

	public String getString(String name)
	{
		makeArgumentsByName();
		return makeString(argumentsByName.get(name), -1, name);
	}

	public String getString(int position, String nullValue)
	{
		return makeString(get(position), nullValue, position, null);
	}

	public String getString(String name, String nullValue)
	{
		makeArgumentsByName();
		return makeString(argumentsByName.get(name), nullValue, -1, name);
	}

	private List makeList(Object value, int position, String name)
	{
		if (value instanceof List)
			return (List)value;
		throw wrongArgumentType("<list>", value, position, name);
	}

	public List getList(int position)
	{
		return makeList(get(position), position, null);
	}

	public List getList(String name)
	{
		makeArgumentsByName();
		return makeList(argumentsByName.get(name), -1, name);
	}

	private Map makeMap(Object value, int position, String name)
	{
		if (value instanceof Map)
			return (Map)value;
		throw wrongArgumentType("<dict>", value, position, name);
	}

	public Map getMap(int position)
	{
		return makeMap(get(position), position, null);
	}

	public Map getMap(String name)
	{
		makeArgumentsByName();
		return makeMap(argumentsByName.get(name), -1, name);
	}

	private Iterator makeIterator(Object value, int position, String name)
	{
		if (value instanceof String)
			return new StringIterator((String)value);
		else if (value instanceof Iterable)
			return ((Iterable)value).iterator();
		else if (value instanceof Map)
			return ((Map)value).keySet().iterator();
		else if (value instanceof Object[])
			return asList((Object[])value).iterator();
		else if (value instanceof Iterator)
			return (Iterator)value;
		throw wrongArgumentType("iterable", value, position, name);
	}

	public Iterator getIterator(int position)
	{
		return makeIterator(get(position), position, null);
	}

	public Iterator getIterator(String name)
	{
		makeArgumentsByName();
		return makeIterator(argumentsByName.get(name), -1, name);
	}

	// /**
	// "Destroys" a {@code BoundArguments} object to simplify the work the Java GC has to do
	// After the call the object is no longer usable.

	// Note that this should not be called for any template arguments,
	// as the variables might be referenced by a closure.
	// However for {@code Function} objects that can be done without problems.
	// **/
	// @Override
	// public void close()
	// {
	// 	if (signature != null)
	// 	{
	// 		if (signature.hasVarKeyword())
	// 		{
	// 			Map<String, Object> kwargs = (Map<String, Object>)get(argumentsByPosition.length - 1);
	// 			kwargs.clear();
	// 		}
	// 		if (signature.hasVarPositional())
	// 		{
	// 			List<Object> args = (List<Object>)get(argumentsByPosition.length - (signature.hasVarKeyword() ? 2 : 1));
	// 			args.clear();
	// 		}
	// 		argumentsByPosition = null;
	// 		if (argumentsByName != null)
	// 		{
	// 			argumentsByName.clear();
	// 			argumentsByName = null;
	// 		}
	// 		signature = null;
	// 	}
	// 	else
	// 	{
	// 		argumentsByName.clear();
	// 		argumentsByName = null;
	// 	}
	// }
}
