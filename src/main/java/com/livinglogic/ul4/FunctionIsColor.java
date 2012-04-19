/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionIsColor implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return (null != args[0]) && (args[0] instanceof Color);
		throw new ArgumentCountMismatchException("function", "iscolor", args.length, 1);
	}

	public String getName()
	{
		return "iscolor";
	}
}
