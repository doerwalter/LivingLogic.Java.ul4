/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionIsTimeDelta implements Function
{
	public String getName()
	{
		return "istimedelta";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		switch (args.length)
		{
			case 1:
				return call(args[0]);
			default:
				throw new ArgumentCountMismatchException("function", "istimedelta", args.length, 1);
		}
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof TimeDelta);
	}
}
