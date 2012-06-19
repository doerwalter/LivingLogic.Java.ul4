/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;

public class FunctionNow implements Function
{
	public String getName()
	{
		return "now";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return call();
		throw new ArgumentCountMismatchException("function", "now", args.length, 0);
	}

	public static Date call()
	{
		return new Date();
	}
}
