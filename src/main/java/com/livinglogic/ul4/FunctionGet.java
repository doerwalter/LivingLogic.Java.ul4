/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class FunctionGet extends NormalFunction
{
	public String getName()
	{
		return "get";
	}

	protected void makeArgumentDescriptions(ArgumentDescriptions argumentDescriptions)
	{
		argumentDescriptions.add("name");
		argumentDescriptions.add("default", null);
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(context.getAllVariables(), args[0], args[1]);
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
