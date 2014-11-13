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
	protected List<String> argumentNames;
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
		argumentNames = null;
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

	private void add(String name)
	{
		arguments.put(name, new ArgumentDescription(name, arguments.size()));
	}

	private void add(String name, Object defaultValue)
	{
		arguments.put(name, new ArgumentDescription(name, arguments.size(), defaultValue));
	}

	public List<String> getArgumentNames()
	{
		if (argumentNames == null)
		{
			argumentNames = new ArrayList<String>(arguments.size() + (remainingArgumentsName != null ? 1 : 0) + (remainingKeywordArgumentsName != null ? 1 : 0));
			for (String argumentName : arguments.keySet())
				argumentNames.add(argumentName);
			if (remainingArgumentsName != null)
				argumentNames.add(remainingArgumentsName);
			if (remainingKeywordArgumentsName != null)
				argumentNames.add(remainingKeywordArgumentsName);
		}
		return argumentNames;
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
}
