/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionFloat implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return Utils.toFloat(args[0]);
		throw new ArgumentCountMismatchException("function", "float", args.length, 1);
	}

	public String getName()
	{
		return "float";
	}
}
