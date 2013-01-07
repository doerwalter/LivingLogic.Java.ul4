/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.IOException;
import java.io.Writer;

public class MethodRender implements Method
{
	public String getName()
	{
		return "render";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args, Map<String, Object> kwargs) throws IOException
	{
		if (args.length > 0)
			throw new PositionalArgumentsNotSupportedException(getName());

		call(context, obj, kwargs);
		return null;
	}

	public static void call(EvaluationContext context, Template obj, Map<String, Object> variables)
	{
		try
		{
			obj.render(context, variables);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static void call(EvaluationContext context, Object obj, Map<String, Object> variables)
	{
		if (obj instanceof Template)
			call(context, (Template)obj, variables);
		else
			throw new UnsupportedOperationException("render() method requires a template!");
	}
}
