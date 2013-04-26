/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.ArrayList;
import static java.util.Arrays.asList;

public class Signature implements Iterable<ArgumentDescription>
{
	protected String name;
	protected LinkedHashMap<String, ArgumentDescription> arguments;
	protected String remainingArguments;
	protected String remainingKeywordArguments;

	public static Object required = new Object();

	public Signature(String name, String remainingArguments, String remainingKeywordArguments, Object... args)
	{
		this.name = name;
		arguments = new LinkedHashMap<String, ArgumentDescription>();
		this.remainingArguments = remainingArguments;
		this.remainingKeywordArguments = remainingKeywordArguments;

		String argname = null;
		for (int i = 0; i < args.length; ++i)
		{
			if (i%2 == 0)
				argname = (String)args[i];
			else
			{
				if (args[i] == required)
					add(argname);
				else
					add(argname, args[i]);
			}
		}
	}

	public Signature(String name)
	{
		this(name, null, null);
	}

	public String getName()
	{
		return name;
	}

	public void add(String name)
	{
		arguments.put(name, new ArgumentDescription(name, arguments.size()));
	}

	public void add(String name, Object defaultValue)
	{
		arguments.put(name, new ArgumentDescription(name, arguments.size(), defaultValue));
	}

	public void setRemainingArguments(String remainingArguments)
	{
		this.remainingArguments = remainingArguments;
	}

	public void setRemainingKeywordArguments(String remainingKeywordArguments)
	{
		this.remainingKeywordArguments = remainingKeywordArguments;
	}

	public Iterator<ArgumentDescription> iterator()
	{
		return arguments.values().iterator();
	}

	public int size()
	{
		return arguments.size();
	}

	public boolean containsArgumentNamed(String argName)
	{
		return arguments.containsKey(argName);
	}

	public Object[] makeArgumentArray(Object[] args, Map<String, Object> kwargs)
	{
		int realSize = size();
		int remainingArgumentsPos = -1;
		int remainingKeywordArgumentsPos = -1;
		if (remainingArguments != null)
			remainingArgumentsPos = realSize++;
		if (remainingKeywordArguments != null)
			remainingKeywordArgumentsPos = realSize++;

		Object[] realargs = new Object[realSize];

		int i = 0;
		for (ArgumentDescription argDesc : this)
		{
			String argName = argDesc.getName();
			Object argValue = kwargs.get(argName);
			// argument has been specified via keyword
			if (argValue != null || kwargs.containsKey(argName))
			{
				if (i < args.length)
					// argument has been specified as a positional argument too
					throw new DuplicateArgumentException(this, argDesc);
				realargs[i] = argValue;
			}
			else
			{
				if (i < args.length)
					// argument has been specified as a positional argument
					realargs[i] = args[i];
				else if (argDesc.hasDefaultValue())
					// we have a default value for this argument
					realargs[i] = argDesc.getDefaultValue();
				else
					throw new MissingArgumentException(this, argDesc);
			}
			++i;
		}

		// Handle additional positional arguments
		// if there are any, and we suport a "*" argument, put the remaining arguments into this argument as a list, else complain
		int expectedArgCount = size();
		if (remainingArguments != null)
		{
			realargs[remainingArgumentsPos] = (args.length > expectedArgCount) ? asList(args).subList(arguments.size(), args.length) : new ArrayList<Object>();
		}
		else
		{
			if (args.length > expectedArgCount)
				throw new TooManyArgumentsException(this, args.length);
		}

		// Handle additional keyword arguments
		// if there are any, and we suport a "**" argument, put the remaining keyword arguments into this argument as a map, else complain
		if (remainingKeywordArguments != null)
		{
			LinkedHashMap<String, Object> realRemainingKeywordArguments = new LinkedHashMap<String, Object>();
			for (String kwargname : kwargs.keySet())
			{
				if (!containsArgumentNamed(kwargname))
				{
					realRemainingKeywordArguments.put(kwargname, kwargs.get(kwargname));
				}
			}
			realargs[remainingKeywordArgumentsPos] = realRemainingKeywordArguments;
		}
		else
		{
			for (String kwargname : kwargs.keySet())
			{
				if (!containsArgumentNamed(kwargname))
					throw new UnsupportedArgumentNameException(this, kwargname);
			}
		}

		return realargs;
	}
}
