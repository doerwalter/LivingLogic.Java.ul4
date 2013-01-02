/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;

public class FunctionIsDate implements Function
{
	public String getName()
	{
		return "isdate";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "isdate", args.length, 1);
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof Date);
	}
}
