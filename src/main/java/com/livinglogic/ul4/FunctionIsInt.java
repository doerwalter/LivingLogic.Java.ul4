/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class FunctionIsInt implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return Utils.isint(args[0]);
		throw new ArgumentCountMismatchException("function", "isint", args.length, 1);
	}

	public String getName()
	{
		return "isint";
	}
}
