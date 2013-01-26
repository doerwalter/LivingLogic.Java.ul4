/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;

public class Signature implements Iterable<ArgumentDescription>
{
	protected String name;
	protected Map<String, ArgumentDescription> arguments;

	public Signature(String name)
	{
		this.name = name;
		arguments = new LinkedHashMap<String, ArgumentDescription>();
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
		Object[] realargs = new Object[size()];

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

		// Check that we don't have any keyword arguments that we don't support
		for (String kwargname : kwargs.keySet())
		{
			if (!containsArgumentNamed(kwargname))
				throw new UnsupportedArgumentNameException(this, kwargname);
		}

		// Check that we don't have more positional arguments than expected
		int expectedArgCount = size();
		if (args.length > expectedArgCount)
			throw new TooManyArgumentsException(this, args.length);

		return realargs;
	}
}
