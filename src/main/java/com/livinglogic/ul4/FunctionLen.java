/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionLen implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return Utils.len(args[0]);
		throw new ArgumentCountMismatchException("function", "len", args.length, 1);
	}

	public String getName()
	{
		return "len";
	}
}