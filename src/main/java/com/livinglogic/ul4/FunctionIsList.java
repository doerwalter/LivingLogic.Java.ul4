/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionIsList implements Function
{
	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof java.util.List) && !(obj instanceof Color);
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "islist", args.length, 1);
	}

	public String getName()
	{
		return "islist";
	}
}
