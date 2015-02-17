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

public class Signature implements Iterable<ArgumentDescription>
{
	protected LinkedHashMap<String, ArgumentDescription> parameters;
	protected List<String> parameterNames;
	protected String remainingParametersName;
	protected String remainingKeywordParametersName;

	/**
	 * Marker objects that specify certain types of parameters.
	 */
	public static Object required = new Object();
	public static Object remainingParameters = new Object();
	public static Object remainingKeywordParameters = new Object();

	public Signature(Object... args)
	{
		parameters = new LinkedHashMap<String, ArgumentDescription>();
		parameterNames = null;
		this.remainingParametersName = null;
		this.remainingKeywordParametersName = null;

		String parameterName = null;
		for (int i = 0; i < args.length; ++i)
		{
			if (i%2 == 0)
				parameterName = (String)args[i];
			else
			{
				if (args[i] == required)
					add(parameterName);
				else if (args[i] == remainingParameters)
					setRemainingParameters(parameterName);
				else if (args[i] == remainingKeywordParameters)
					setRemainingKeywordParameters(parameterName);
				else
					add(parameterName, args[i]);
			}
		}
	}

	public void add(String name)
	{
		parameters.put(name, new ArgumentDescription(name, parameters.size()));
	}

	public void add(String name, Object defaultValue)
	{
		parameters.put(name, new ArgumentDescription(name, parameters.size(), defaultValue));
	}

	public void setRemainingParameters(String name)
	{
		remainingParametersName = name;
	}

	public void setRemainingKeywordParameters(String name)
	{
		remainingKeywordParametersName = name;
	}

	public List<String> getParameterNames()
	{
		if (parameterNames == null)
		{
			parameterNames = new ArrayList<String>(parameters.size() + (remainingParametersName != null ? 1 : 0) + (remainingKeywordParametersName != null ? 1 : 0));
			for (String argumentName : parameters.keySet())
				parameterNames.add(argumentName);
			if (remainingParametersName != null)
				parameterNames.add(remainingParametersName);
			if (remainingKeywordParametersName != null)
				parameterNames.add(remainingKeywordParametersName);
		}
		return parameterNames;
	}

	public Iterator<ArgumentDescription> iterator()
	{
		return parameters.values().iterator();
	}

	public int size()
	{
		return parameters.size();
	}

	public boolean containsParameterNamed(String argName)
	{
		return parameters.containsKey(argName);
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

		if (remainingParametersName != null)
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append("*");
			buffer.append(remainingParametersName);
		}

		if (remainingKeywordParametersName != null)
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append("**");
			buffer.append(remainingKeywordParametersName);
		}
		buffer.append(")");

		return buffer.toString();
	}
}
