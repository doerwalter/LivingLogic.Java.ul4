/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionRGB implements Function
{
	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 3)
			return Utils.rgb(args[0], args[1], args[2]);
		else if (args.length == 4)
			return Utils.rgb(args[0], args[1], args[2], args[3]);
		throw new ArgumentCountMismatchException("function", "rgb", args.length, 3, 4);
	}

	public String getName()
	{
		return "rgb";
	}
}
