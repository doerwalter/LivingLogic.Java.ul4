/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionMonthDelta implements Function
{
	public String getName()
	{
		return "monthdelta";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		switch (args.length)
		{
			case 0:
				return call();
			case 1:
				return call(args[0]);
			default:
				throw new ArgumentCountMismatchException("function", "monthdelta", args.length, 0, 1);
		}
	}

	public static MonthDelta call()
	{
		return new MonthDelta();
	}

	public static MonthDelta call(int months)
	{
		return new MonthDelta(months);
	}

	public static MonthDelta call(Object months)
	{
		return call(Utils.toInt(months));
	}
}
