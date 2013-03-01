/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.Writer;

public class MethodRender implements Method
{
	public String nameUL4()
	{
		return "render";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args, Map<String, Object> kwargs)
	{
		if (args.length > 0)
			throw new PositionalArgumentsNotSupportedException(nameUL4());

		call(context, obj, kwargs);
		return null;
	}

	public static void call(EvaluationContext context, Template obj, Map<String, Object> variables)
	{
		obj.render(context, variables);
	}

	public static void call(EvaluationContext context, Object obj, Map<String, Object> variables)
	{
		if (obj instanceof Template)
			call(context, (Template)obj, variables);
		else
			throw new UnsupportedOperationException("render() method requires a template!");
	}
}
