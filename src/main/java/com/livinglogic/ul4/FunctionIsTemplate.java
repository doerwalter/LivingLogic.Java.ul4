/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionIsTemplate implements Function
{
	public String getName()
	{
		return "istemplate";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "istemplate", args.length, 1);
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof Template);
	}
}
