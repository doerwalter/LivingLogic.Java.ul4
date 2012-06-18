/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;

public class FunctionIsDate implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return Utils.isdate(args[0]);
		throw new ArgumentCountMismatchException("function", "isdate", args.length, 1);
	}

	public String getName()
	{
		return "isdate";
	}
}
