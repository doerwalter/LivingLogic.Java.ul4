/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionIsBool implements Function
{
	public String getName()
	{
		return "isbool";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "isbool", args.length, 1);
	}

	public static boolean call(Object obj)
	{
		return (obj != null) && (obj instanceof Boolean);
	}
}
