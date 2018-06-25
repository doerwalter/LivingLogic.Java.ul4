/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
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

public class Signature implements UL4Repr, Iterable<ParameterDescription>
{
	/**
	 * All parameters in the order they were specified in the constructor.
	 */
	protected LinkedHashMap<String, ParameterDescription> parameters;

	/**
	 * The number of parameters (excluding the {@code *} and {@code **} parameters).
	 */
	protected int size;

	/**
	 * Does the signature have a {@code *} parameter (which collections any
	 * additional positional argument)?
	 */
	protected boolean hasRemainingParameters;

	/**
	 * Does the signature have a {@code **} parameter (which collections any
	 * additional keyword argument)?
	 */
	protected boolean hasRemainingKeywordParameters;
	protected List<String> parameterNames;

	/**
	 * Marker object that specifies that this parameter is required.
	 */
	public static Object required = new Object();

	/**
	 * Marker object that specifies that this parameter collects any additional
	 * positional argument.
	 */
	public static Object remainingParameters = new Object();

	/**
	 * Marker object that specifies that this parameter collects any additional
	 * keyword argument.
	 */
	public static Object remainingKeywordParameters = new Object();

	public Signature(Object... args)
	{
		parameters = new LinkedHashMap<String, ParameterDescription>();
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
					add(parameterName, ParameterDescription.Type.REQUIRED, null);
				else if (args[i] == remainingParameters)
					add(parameterName, ParameterDescription.Type.VAR_POSITIONAL, null);
				else if (args[i] == remainingKeywordParameters)
					add(parameterName, ParameterDescription.Type.VAR_KEYWORD, null);
				else
					add(parameterName, ParameterDescription.Type.DEFAULT, args[i]);
			}
		}
	}

	public void add(String name, ParameterDescription.Type type, Object defaultValue)
	{
		parameters.put(name, new ParameterDescription(name, parameters.size(), type, defaultValue));
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

	public Collection<ParameterDescription> getParameters()
	{
		return parameters.values();
	}

	public Iterator<ParameterDescription> iterator()
	{
		return parameters.values().iterator();
	}

	public int size()
	{
		return size;
	}

	public boolean containsParameterNamed(String argName)
	{
		ParameterDescription description = parameters.get(argName);
		if (description == null)
			return false;
		ParameterDescription.Type type = description.getType();
		return type == ParameterDescription.Type.REQUIRED || type == ParameterDescription.Type.DEFAULT;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" ");
		formatter.append(toString());
		formatter.append(">");
	}

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		boolean first = true;

		buffer.append("(");

		for (ParameterDescription paramDesc : parameters.values())
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(paramDesc);
		}
		buffer.append(")");

		return buffer.toString();
	}
}
