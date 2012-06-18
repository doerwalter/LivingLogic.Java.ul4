/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodMinute implements Method
{
	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return Utils.minute(obj);
			default:
				throw new ArgumentCountMismatchException("method", "minute", args.length, 0);
		}
	}

	public String getName()
	{
		return "minute";
	}
}
