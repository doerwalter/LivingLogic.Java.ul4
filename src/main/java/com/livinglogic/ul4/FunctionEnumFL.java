/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionEnumFL implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return Utils.enumfl(args[0]);
		else if (args.length == 2)
			return Utils.enumfl(args[0], args[1]);
		throw new ArgumentCountMismatchException("function", "enumfl", args.length, 1, 2);
	}

	public String getName()
	{
		return "enumfl";
	}
}
