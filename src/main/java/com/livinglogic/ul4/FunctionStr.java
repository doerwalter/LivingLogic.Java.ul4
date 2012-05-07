/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionStr implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return "";
		else if (args.length == 1)
			return Utils.str(args[0]);
		throw new ArgumentCountMismatchException("function", "str", args.length, 0, 1);
	}

	public String getName()
	{
		return "str";
	}
}
