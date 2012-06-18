/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionVars implements Function
{
	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return context.getVariables();
		throw new ArgumentCountMismatchException("function", "vars", args.length, 0);
	}

	public String getName()
	{
		return "vars";
	}
}
