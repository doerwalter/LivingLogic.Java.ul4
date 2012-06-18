/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;

public class FunctionNow implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return Utils.now();
		throw new ArgumentCountMismatchException("function", "now", args.length, 0);
	}

	public String getName()
	{
		return "now";
	}
}
