/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionIsStr implements Function
{
	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof String);
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "isstr", args.length, 1);
	}

	public String getName()
	{
		return "isstr";
	}
}
