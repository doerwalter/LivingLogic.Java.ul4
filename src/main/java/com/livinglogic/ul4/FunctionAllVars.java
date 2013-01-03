/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class FunctionAllVars implements Function
{
	public String getName()
	{
		return "allvars";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return call(context.getAllVariables());
		throw new ArgumentCountMismatchException("function", "allvars", args.length, 0);
	}

	public static Object call(Map<String, Object> variables)
	{
		return variables;
	}
}
