/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;

public class FunctionIsFloat implements Function
{
	public String getName()
	{
		return "isfloat";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "isfloat", args.length, 1);
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof BigDecimal || obj instanceof Float || obj instanceof Double);
	}
}
