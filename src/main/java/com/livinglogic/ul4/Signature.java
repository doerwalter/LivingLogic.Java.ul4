/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
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
import java.util.Collection;

public class Signature implements Iterable<ArgumentDescription>
{
	protected LinkedHashMap<String, ArgumentDescription> parameters;
	protected int size;
	protected boolean hasRemainingParameters;
	protected boolean hasRemainingKeywordParameters;
	protected List<String> parameterNames;

	/**
	 * Marker objects that specify certain types of parameters.
	 */
	public static Object required = new Object();
	public static Object remainingParameters = new Object();
	public static Object remainingKeywordParameters = new Object();

	public Signature(Object... args)
	{
		parameters = new LinkedHashMap<String, ArgumentDescription>();
		size = 0;
		hasRemainingParameters = false;
		hasRemainingKeywordParameters = false;
		parameterNames = null;

		String parameterName = null;
		for (int i = 0; i < args.length; ++i)
		{
			if (i%2 == 0)
				parameterName = (String)args[i];
			else
			{
				if (args[i] == required)
					add(parameterName, ArgumentDescription.Type.REQUIRED, null);
				else if (args[i] == remainingParameters)
					add(parameterName, ArgumentDescription.Type.VAR_POSITIONAL, null);
				else if (args[i] == remainingKeywordParameters)
					add(parameterName, ArgumentDescription.Type.VAR_KEYWORD, null);
				else
					add(parameterName, ArgumentDescription.Type.DEFAULT, args[i]);
			}
		}
	}

	public void add(String name, ArgumentDescription.Type type, Object defaultValue)
	{
		parameters.put(name, new ArgumentDescription(name, parameters.size(), type, defaultValue));
		switch (type)
		{
			case REQUIRED:
			case DEFAULT:
				++size;
				break;
			case VAR_POSITIONAL:
				hasRemainingParameters = true;
				break;
			case VAR_KEYWORD:
				hasRemainingKeywordParameters = true;
				break;
		}
	}

	public boolean hasRemainingParameters()
	{
		return hasRemainingParameters;
	}

	public boolean hasRemainingKeywordParameters()
	{
		return hasRemainingKeywordParameters;
	}

	public Collection<ArgumentDescription> getParameters()
	{
		return parameters.values();
	}

	public Iterator<ArgumentDescription> iterator()
	{
		return parameters.values().iterator();
	}

	public int size()
	{
		return size;
	}

	public boolean containsParameterNamed(String argName)
	{
		ArgumentDescription description = parameters.get(argName);
		if (description == null)
			return false;
		ArgumentDescription.Type type = description.getType();
		return type == ArgumentDescription.Type.REQUIRED || type == ArgumentDescription.Type.DEFAULT;
	}

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		boolean first = true;

		buffer.append("(");

		for (ArgumentDescription argdesc : parameters.values())
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(argdesc);
		}
		buffer.append(")");

		return buffer.toString();
	}
}
