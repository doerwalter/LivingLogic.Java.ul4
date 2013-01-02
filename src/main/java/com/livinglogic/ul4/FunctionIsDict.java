/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class FunctionIsDict implements Function
{
	public String getName()
	{
		return "isdict";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "isdict", args.length, 1);
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof Map) && !(obj instanceof Template);
	}
}
