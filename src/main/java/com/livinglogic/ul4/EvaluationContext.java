/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.Writer;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class EvaluationContext
{
	protected Writer writer;
	protected Map<String, Object> variables;

	public EvaluationContext(Writer writer)
	{
		this(writer, null);
	}

	public EvaluationContext(Writer writer, Map<String, Object> variables)
	{
		this.writer = writer;
		if (variables == null)
			variables = new HashMap<String, Object>();
		this.variables = variables;
	}

	public Map<String, Object> getVariables()
	{
		return variables;
	}

	public Writer getWriter()
	{
		return writer;
	}

	public void write(String string) throws IOException
	{
		writer.write(string);
	}

	public void put(String key, Object value)
	{
		variables.put(key, value);
	}

	public Object get(String key)
	{
		return Utils.getItem(variables, key);
	}

	public void remove(String key)
	{
		variables.remove(key);
	}
}
