/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class FunctionIsInt implements Function
{
	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof BigInteger || obj instanceof Byte || obj instanceof Integer || obj instanceof Long || obj instanceof Short);
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "isint", args.length, 1);
	}

	public String getName()
	{
		return "isint";
	}
}
