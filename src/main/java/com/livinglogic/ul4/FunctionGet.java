/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class FunctionGet implements Function
{
	public String getName()
	{
		return "get";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(context.getVariables(), args[0]);
		else if (args.length == 2)
			return call(context.getVariables(), args[0], args[1]);
		throw new ArgumentCountMismatchException("function", "get", args.length, 1, 2);
	}

	public static Object call(Map<String, Object> variables, Object key)
	{
		return variables.get(key);
	}

	public static Object call(Map<String, Object> variables, Object key, Object defaultValue)
	{
		Object result = variables.get(key);
		if (result == null && !variables.containsKey(key))
			result = defaultValue;
		return result;
	}
}
