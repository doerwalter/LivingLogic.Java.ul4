/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;

public class FunctionFormat implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 2)
			return Utils.format(args[0], args[1]);
		throw new ArgumentCountMismatchException("function", "format", args.length, 0);
	}

	public String getName()
	{
		return "format";
	}
}
