/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;

public class FunctionZip implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length >= 2)
			return Utils.zip(args);
		throw new ArgumentCountMismatchException("function", "zip", args.length, 2, 999);
	}

	public String getName()
	{
		return "zip";
	}
}
