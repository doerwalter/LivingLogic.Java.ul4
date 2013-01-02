/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionIsMonthDelta implements Function
{
	public String getName()
	{
		return "ismonthdelta";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		switch (args.length)
		{
			case 1:
				return call(args[0]);
			default:
				throw new ArgumentCountMismatchException("function", "ismonthdelta", args.length, 1);
		}
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof MonthDelta);
	}
}
