/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodRStrip implements Method
{
	public Object call(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return Utils.rstrip(obj);
			case 1:
				return Utils.rstrip(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "rstrip", args.length, 0, 1);
		}
	}

	public String getName()
	{
		return "rstrip";
	}
}
