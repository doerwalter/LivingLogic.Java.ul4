/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class FunctionIsDict implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return (null != args[0]) && (args[0] instanceof Map) && !(args[0] instanceof Template);
		throw new ArgumentCountMismatchException("function", "isdict", args.length, 1);
	}

	public String getName()
	{
		return "isdict";
	}
}
