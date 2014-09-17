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
import java.util.LinkedHashMap;
import java.util.Map;

public class Signature implements Iterable<ArgumentDescription>
{
	protected LinkedHashMap<String, ArgumentDescription> arguments;
	protected String remainingArgumentsName;
	protected String remainingKeywordArgumentsName;

	/**
	 * Marker objects that specify certain types of arguments.
	 */
	public static Object required = new Object();
	public static Object remainingArguments = new Object();
	public static Object remainingKeywordArguments = new Object();

	public Signature(Object... args)
	{
		arguments = new LinkedHashMap<String, ArgumentDescription>();
		this.remainingArgumentsName = null;
		this.remainingKeywordArgumentsName = null;

		String argname = null;
		for (int i = 0; i < args.length; ++i)
		{
			if (i%2 == 0)
				argname = (String)args[i];
			else
			{
				if (args[i] == required)
					add(argname);
				else if (args[i] == remainingArguments)
					this.remainingArgumentsName = argname;
				else if (args[i] == remainingKeywordArguments)
					this.remainingKeywordArgumentsName = argname;
				else
					add(argname, args[i]);
			}
		}
	}

	public void add(String name)
	{
		arguments.put(name, new ArgumentDescription(name, arguments.size()));
	}

	public void add(String name, Object defaultValue)
	{
		arguments.put(name, new ArgumentDescription(name, arguments.size(), defaultValue));
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

	public List<Object> makeArgumentList(UL4Name object, List<Object> args, Map<String, Object> kwargs)
	{
		List<Object> realargs = new ArrayList<Object>(size());

		int i = 0;
		for (ArgumentDescription argDesc : this)
		{
			String argName = argDesc.getName();
			Object argValue = kwargs.get(argName);
			// argument has been specified via keyword
			if (argValue != null || kwargs.containsKey(argName))
			{
				if (i < args.size())
					// argument has been specified as a positional argument too
					throw new DuplicateArgumentException(object, argDesc);
				realargs.add(argValue);
			}
			else
			{
				if (i < args.size())
					// argument has been specified as a positional argument
					realargs.add(args.get(i));
				else if (argDesc.hasDefaultValue())
					// we have a default value for this argument
					realargs.add(argDesc.getDefaultValue());
				else
					throw new MissingArgumentException(object, argDesc);
			}
			++i;
		}

		// Handle additional positional arguments
		// if there are any, and we suport a "*" argument, put the remaining arguments into this argument as a list, else complain
		int expectedArgCount = size();
		if (remainingArgumentsName != null)
		{
			realargs.add((args.size() > expectedArgCount) ? args.subList(arguments.size(), args.size()) : new ArrayList<Object>());
		}
		else
		{
			if (args.size() > expectedArgCount)
				throw new TooManyArgumentsException(object, this, args.size());
		}

		// Handle additional keyword arguments
		// if there are any, and we suport a "**" argument, put the remaining keyword arguments into this argument as a map, else complain
		if (remainingKeywordArgumentsName != null)
		{
			LinkedHashMap<String, Object> realRemainingKeywordArguments = new LinkedHashMap<String, Object>();
			for (String kwargname : kwargs.keySet())
			{
				if (!containsArgumentNamed(kwargname))
				{
					realRemainingKeywordArguments.put(kwargname, kwargs.get(kwargname));
				}
			}
			realargs.add(realRemainingKeywordArguments);
		}
		else
		{
			for (String kwargname : kwargs.keySet())
			{
				if (!containsArgumentNamed(kwargname))
					throw new UnsupportedArgumentNameException(object, kwargname);
			}
		}

		return realargs;
	}
}
