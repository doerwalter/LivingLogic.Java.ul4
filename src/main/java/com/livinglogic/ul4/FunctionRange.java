/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionRange implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		switch (args.length)
		{
			case 1:
				return Utils.range(args[0]);
			case 2:
				return Utils.range(args[0], args[1]);
			case 3:
				return Utils.range(args[0], args[1], args[2]);
			default:
				throw new ArgumentCountMismatchException("function", "range", args.length, 1, 3);
		}
	}

	public String getName()
	{
		return "range";
	}
}
