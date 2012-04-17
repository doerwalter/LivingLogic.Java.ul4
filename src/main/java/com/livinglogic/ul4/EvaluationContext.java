/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.Writer;
import java.util.Map;
import java.util.Locale;
import java.io.IOException;

public class EvaluationContext
{
	protected Writer writer;
	protected Map<String, Object> variables;
	Locale locale;

	public EvaluationContext(Writer writer, Map<String, Object> variables, Locale locale)
	{
		this.writer = writer;
		this.variables = variables;
		this.locale = locale;
	}

	public Map<String, Object> getVariables()
	{
		return variables;
	}

	public Writer getWriter()
	{
		return writer;
	}

	public Locale getLocale()
	{
		return locale;
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
