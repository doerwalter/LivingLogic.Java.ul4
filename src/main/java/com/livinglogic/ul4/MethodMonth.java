/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodMonth implements Method
{
	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return Utils.month(obj);
			default:
				throw new ArgumentCountMismatchException("method", "month", args.length, 0);
		}
	}

	public String getName()
	{
		return "month";
	}
}
