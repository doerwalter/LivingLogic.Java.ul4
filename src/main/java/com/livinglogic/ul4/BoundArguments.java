/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
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
			if (args != null && args.size() > 0)
				throw new PositionalArgumentsNotSupportedException(object);

			argumentsByPosition = null;
			argumentsByName = kwargs != null ? new LinkedHashMap<String, Object>(kwargs) : new LinkedHashMap<String, Object>();
		}
		else
		{
			int size = signature.size() + (signature.hasRemainingParameters() ? 1 : 0) + (signature.hasRemainingKeywordParameters() ? 1 : 0);;
			argumentsByPosition = new ArrayList<Object>(size);
			argumentsByName = null; // will be created on demand

			int argsize = args != null ? args.size() : 0;

			String remainingArgumentsName = null;
			String remainingKeywordArgumentsName = null;

			int i = 0;
			for (ParameterDescription paramDesc : signature)
			{
				String argName = paramDesc.getName();
				ParameterDescription.Type type = paramDesc.getType();
				if (type == ParameterDescription.Type.VAR_POSITIONAL)
					remainingArgumentsName = argName;
				else if (type == ParameterDescription.Type.VAR_KEYWORD)
					remainingKeywordArgumentsName = argName;
				else
				{
					Object argValue = kwargs != null ? kwargs.get(argName) : null;
					// parameter has been specified via keyword
					if (argValue != null || (kwargs != null && kwargs.containsKey(argName)))
					{
						if (i < argsize)
							// parameter has been specified as a positional argument too
							throw new DuplicateArgumentException(object, paramDesc);
						add(argValue);
					}
					else
					{
						if (i < argsize)
							// parameter has been specified as a positional argument
							add(args.get(i));
						else if (type == ParameterDescription.Type.DEFAULT)
							// we have a default value for this parameter
							add(paramDesc.getDefaultValue());
						else
							throw new MissingArgumentException(object, paramDesc);
					}
				}
				++i;
			}

			// Handle additional positional arguments
			// if there are any, and we support a "*" parameter, put the remaining arguments into this argument as a list, else complain
			int expectedArgCount = signature.size();
			if (argsize > expectedArgCount)
			{
				if (remainingArgumentsName != null)
					add(args.subList(expectedArgCount, argsize));
				else
					throw new TooManyArgumentsException(object, signature, argsize);
			}
			else
			{
				if (remainingArgumentsName != null)
					add(new ArrayList<Object>());
			}

			// Handle additional keyword arguments
			// if there are any, and we support a "**" parameter, put the remaining keyword arguments into this argument as a map, else complain
			if (kwargs != null)
			{
				if (remainingKeywordArgumentsName != null)
				{
					LinkedHashMap<String, Object> realRemainingKeywordArguments = new LinkedHashMap<String, Object>();
					for (String kwargname : kwargs.keySet())
					{
						if (!signature.containsParameterNamed(kwargname))
						{
							realRemainingKeywordArguments.put(kwargname, kwargs.get(kwargname));
						}
					}
					add(realRemainingKeywordArguments);
				}
				else
				{
					for (String kwargname : kwargs.keySet())
					{
						if (!signature.containsParameterNamed(kwargname))
							throw new UnsupportedArgumentNameException(object, kwargname);
					}
				}
			}
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
			for (ParameterDescription paramDesc : signature.getParameters())
				argumentsByName.put(paramDesc.getName(), argumentsByPosition.get(i++));
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

	private void add(Object value)
	{
		argumentsByPosition.add(value);
	}

	/**
	 * "Destroys" a {@code BoundArguments} object to simplify the work the Java GC has to do
	 * After the call the object is no longer usable.
	 *
	 * Note that this should not be called for any template arguments,
	 * as the variables might be referenced by a closure.
	 * However for {@code Function} objects that can be done without problems.
	 */
	public void close()
	{
		if (signature != null)
		{
			if (signature.hasRemainingKeywordParameters())
			{
				Map<String, Object> kwargs = (Map<String, Object>)get(argumentsByPosition.size() - 1);
				kwargs.clear();
			}
			if (signature.hasRemainingParameters())
			{
				List<Object> args = (List<Object>)get(argumentsByPosition.size() - (signature.hasRemainingKeywordParameters() ? 2 : 1));
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
