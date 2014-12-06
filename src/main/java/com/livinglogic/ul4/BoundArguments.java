/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
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

public class BoundArguments
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
			int size = signature.size() + (signature.remainingParametersName != null ? 1 : 0) + (signature.remainingKeywordParametersName != null ? 1 : 0);
			argumentsByPosition = new ArrayList<Object>(size);
			argumentsByName = null; // will be created on demand

			int argsize = args != null ? args.size() : 0;

			int i = 0;
			for (ArgumentDescription argDesc : signature)
			{
				String argName = argDesc.getName();
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
					else if (argDesc.hasDefaultValue())
						// we have a default value for this argument
						add(argDesc.getDefaultValue());
					else
						throw new MissingArgumentException(object, argDesc);
				}
				++i;
			}

			// Handle additional positional arguments
			// if there are any, and we suport a "*" argument, put the remaining arguments into this argument as a list, else complain
			int expectedArgCount = signature.size();
			if (signature.remainingParametersName != null)
			{
				add(argsize > expectedArgCount ? args.subList(signature.size(), argsize) : new ArrayList<Object>());
			}
			else
			{
				if (argsize > expectedArgCount)
					throw new TooManyArgumentsException(object, signature, argsize);
			}

			// Handle additional keyword arguments
			// if there are any, and we suport a "**" argument, put the remaining keyword arguments into this argument as a map, else complain
			if (kwargs != null)
			{
				if (signature.remainingKeywordParametersName != null)
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

			List<String> parameterNames = signature.getParameterNames();

			for (int i = 0; i < parameterNames.size(); ++i)
				argumentsByName.put(parameterNames.get(i), argumentsByPosition.get(i));
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
	 * After the call the object is no longer usable
	 */
	public void cleanup()
	{
		if (signature != null)
		{
			if (signature.remainingKeywordParametersName != null)
			{
				Map<String, Object> kwargs = (Map<String, Object>)get(argumentsByPosition.size() - 1);
				kwargs.clear();
			}
			if (signature.remainingParametersName != null)
			{
				List<Object> args = (List<Object>)get(argumentsByPosition.size() - (signature.remainingKeywordParametersName != null ? 2 : 1));
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
