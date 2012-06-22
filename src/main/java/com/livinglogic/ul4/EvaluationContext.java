/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.Writer;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.io.IOException;

public class EvaluationContext
{
	protected Writer writer;
	protected Map<String, Object> variables;
	protected Stack<Object> stack = new Stack<Object>(); // used as temporary storage space for the ``and`` and ``or`` operators in compiled mode

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
		Object result = variables.get(key);

		if ((result == null) && !variables.containsKey(key))
			throw new KeyException(key);
		return result;
	}

	public Object get(String key, Object defaultValue)
	{
		Object result = variables.get(key);

		if ((result == null) && !variables.containsKey(key))
			return defaultValue;
		return result;
	}

	public void remove(String key)
	{
		variables.remove(key);
	}

	public Object push(Object object)
	{
		stack.push(object);
		return object;
	}

	public Object pop()
	{
		return stack.pop();
	}

	public Object pop(Object object)
	{
		stack.pop();
		return object;
	}
}
