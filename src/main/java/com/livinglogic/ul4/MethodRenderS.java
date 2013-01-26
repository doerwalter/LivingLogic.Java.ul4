/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.IOException;

public class MethodRenderS implements Method
{
	public String nameUL4()
	{
		return "renders";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args, Map<String, Object> kwargs) throws IOException
	{
		if (args.length > 0)
			throw new PositionalArgumentsNotSupportedException(nameUL4());
		return call(context, obj, kwargs);
	}

	public static String call(EvaluationContext context, Template obj, Map<String, Object> variables)
	{
		return obj.renders(context, variables);
	}

	public static String call(EvaluationContext context, Object obj, Map<String, Object> variables)
	{
		if (obj instanceof Template)
			return call(context, (Template)obj, variables);
		throw new UnsupportedOperationException("renders() method requires a template!");
	}
}
