/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static java.util.Arrays.asList;

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
			argumentsByName = kwargs != null ? new HashMap<String, Object>(kwargs) : new HashMap<String, Object>();
		}
		else
		{
			int size = signature.size() + (signature.hasRemainingParameters() ? 1 : 0) + (signature.hasRemainingKeywordParameters() ? 1 : 0);;
			argumentsByPosition = new ArrayList<Object>(size);
			argumentsByName = null; // will be created on demand

			int argsize = args != null ? args.size() : 0;

			String remainingParametersName = null;
			String remainingKeywordParametersName = null;

			int i = 0;
			for (ArgumentDescription argDesc : signature)
			{
				String argName = argDesc.getName();
				ArgumentDescription.Type type = argDesc.getType();
				if (type == ArgumentDescription.Type.VAR_POSITIONAL)
					remainingParametersName = argName;
				else if (type == ArgumentDescription.Type.VAR_KEYWORD)
					remainingKeywordParametersName = argName;
				else
				{
					Object argValue = kwargs != null ? kwargs.get(argName) : null;
					// argument has been specified via keyword
					if (argValue != null || (kwargs != null && kwargs.containsKey(argName)))
					{
						if (i < argsize)
							// argument has been specified as a positional argument too
							throw new DuplicateArgumentException(object, argDesc);
						add(argValue);
					}
					else
					{
						if (i < argsize)
							// argument has been specified as a positional argument
							add(args.get(i));
						else if (type == ArgumentDescription.Type.DEFAULT)
							// we have a default value for this argument
							add(argDesc.getDefaultValue());
						else
							throw new MissingArgumentException(object, argDesc);
					}
				}
				++i;
			}

			// Handle additional positional arguments
			// if there are any, and we support a "*" argument, put the remaining arguments into this argument as a list, else complain
			int expectedArgCount = signature.size();
			if (argsize > expectedArgCount)
			{
				if (remainingParametersName != null)
					add(args.subList(expectedArgCount, argsize));
				else
					throw new TooManyArgumentsException(object, signature, argsize);
			}
			else
			{
				if (remainingParametersName != null)
					add(new ArrayList<Object>());
			}

			// Handle additional keyword arguments
			// if there are any, and we support a "**" argument, put the remaining keyword arguments into this argument as a map, else complain
			if (kwargs != null)
			{
				if (remainingKeywordParametersName != null)
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
			for (ArgumentDescription argDesc : signature.getParameters())
				argumentsByName.put(argDesc.getName(), argumentsByPosition.get(i++));
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
