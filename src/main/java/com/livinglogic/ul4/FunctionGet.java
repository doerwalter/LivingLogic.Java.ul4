/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionGet implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return Utils.get(context.getVariables(), args[0]);
		else if (args.length == 2)
			return Utils.get(context.getVariables(), args[0], args[1]);
		throw new ArgumentCountMismatchException("function", "get", args.length, 1);
	}

	public String getName()
	{
		return "get";
	}
}