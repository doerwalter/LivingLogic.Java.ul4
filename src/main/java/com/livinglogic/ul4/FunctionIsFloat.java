/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;

public class FunctionIsFloat implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return (null != args[0]) && (args[0] instanceof BigDecimal || args[0] instanceof Float || args[0] instanceof Double);
		throw new ArgumentCountMismatchException("function", "isfloat", args.length, 1);
	}

	public String getName()
	{
		return "isfloat";
	}
}
