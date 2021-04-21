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

public class BoundArguments implements AutoCloseable
{
	protected Signature signature;
	protected List<Object> argumentsByPosition;
	protected Map<String, Object> argumentsByName;

	public BoundArguments(Signature signature, UL4Name object, List<Object> args, Map<String, Object> kwargs)
	{
		this.signature = signature;
		if (signature == null)
		{
			// If we don't have a signature we only support keyword arguments
			if (args != null && args.size() > 0)
				throw new PositionalArgumentsNotSupportedException(object);

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

			argumentsByPosition = new ArrayList<Object>(count + (varPositionalParameter != null ? 1 : 0) + (varKeywordParameter != null ? 1 : 0));
			for (i = 0; i < count; ++i)
				argumentsByPosition.add(null);
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
						argumentsByPosition.set(i, argValue);
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
							throw new TooManyArgumentsException(object, signature, args.size());
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
							throw new DuplicateArgumentException(object, param);
						else
						{
							argumentsByPosition.set(position, argValue);
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
								throw new DuplicateArgumentException(object, argName);
							varKeywordArguments.put(argName, argValue);
						}
						else
							// else complain
							throw new UnsupportedArgumentNameException(object, argName);
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
						argumentsByPosition.set(i, param.getDefaultValue());
						haveValue[i] = true;
					}
					else
						throw new MissingArgumentException(object, param);
				}
				++i;
			}

			// Set variable parameters
			if (varPositionalArguments != null)
				argumentsByPosition.add(varPositionalArguments);
			if (varKeywordArguments != null)
				argumentsByPosition.add(varKeywordArguments);
		}
	}

	public List<Object> byPosition()
	{
		return argumentsByPosition;
	}

	private void makeArgumentsByName()
	{
		if (argumentsByName == null)
		{
			argumentsByName = new LinkedHashMap<String, Object>(argumentsByPosition.size());

			int i = 0;
			for (ParameterDescription param : signature.getParametersByPosition())
				argumentsByName.put(param.getName(), argumentsByPosition.get(i++));

			ParameterDescription varPositional = signature.getVarPositional();
			if (varPositional != null)
				argumentsByName.put(varPositional.getName(), argumentsByPosition.get(i++));

			ParameterDescription varKeyword = signature.getVarKeyword();
			if (varKeyword != null)
				argumentsByName.put(varKeyword.getName(), argumentsByPosition.get(i++));
		}
	}

	public Map<String, Object> byName()
	{
		makeArgumentsByName();
		return argumentsByName;
	}

	public Object get(int position)
	{
		return argumentsByPosition.get(position);
	}

	public Object get(String name)
	{
		makeArgumentsByName();
		return argumentsByName.get(name);
	}

	/**
	"Destroys" a {@code BoundArguments} object to simplify the work the Java GC has to do
	After the call the object is no longer usable.

	Note that this should not be called for any template arguments,
	as the variables might be referenced by a closure.
	However for {@code Function} objects that can be done without problems.
	**/
	public void close()
	{
		if (signature != null)
		{
			if (signature.hasVarKeyword())
			{
				Map<String, Object> kwargs = (Map<String, Object>)get(argumentsByPosition.size() - 1);
				kwargs.clear();
			}
			if (signature.hasVarPositional())
			{
				List<Object> args = (List<Object>)get(argumentsByPosition.size() - (signature.hasVarKeyword() ? 2 : 1));
				args.clear();
			}
			argumentsByPosition.clear();
			argumentsByPosition = null;
			if (argumentsByName != null)
			{
				argumentsByName.clear();
				argumentsByName = null;
			}
			signature = null;
		}
		else
		{
			argumentsByName.clear();
			argumentsByName = null;
		}
	}
}
