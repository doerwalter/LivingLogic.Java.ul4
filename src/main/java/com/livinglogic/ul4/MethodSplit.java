/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodSplit implements Method
{
	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return Utils.split(obj);
			case 1:
				return Utils.split(obj, args[0]);
			case 2:
				return Utils.split(obj, args[0], args[1]);
			default:
				throw new ArgumentCountMismatchException("method", "split", args.length, 0, 2);
		}
	}

	public String getName()
	{
		return "split";
	}
}
