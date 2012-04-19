/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionRandom implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return Utils.random();
		throw new ArgumentCountMismatchException("function", "random", args.length, 0);
	}

	public String getName()
	{
		return "random";
	}
}
