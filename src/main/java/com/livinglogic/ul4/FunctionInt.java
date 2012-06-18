/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionInt implements Function
{
	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return 0;
		else if (args.length == 1)
			return Utils.toInteger(args[0]);
		else if (args.length == 2)
			return Utils.toInteger(args[0], args[1]);
		throw new ArgumentCountMismatchException("function", "int", args.length, 0, 2);
	}

	public String getName()
	{
		return "int";
	}
}
