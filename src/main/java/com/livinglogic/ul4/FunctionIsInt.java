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
			return (null != args[0]) && (args[0] instanceof BigInteger || args[0] instanceof Byte || args[0] instanceof Integer || args[0] instanceof Long || args[0] instanceof Short);
		throw new ArgumentCountMismatchException("function", "isint", args.length, 1);
	}

	public String getName()
	{
		return "isint";
	}
}
