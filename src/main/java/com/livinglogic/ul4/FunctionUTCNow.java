/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionUTCNow implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return Utils.utcnow();
		throw new ArgumentCountMismatchException("function", "utcnow", args.length, 0);
	}

	public String getName()
	{
		return "utcnow";
	}
}
