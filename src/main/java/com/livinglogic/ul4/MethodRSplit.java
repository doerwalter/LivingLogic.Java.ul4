/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodRSplit implements Method
{
	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return Utils.rsplit(obj);
			case 1:
				return Utils.rsplit(obj, args[0]);
			case 2:
				return Utils.rsplit(obj, args[0], args[1]);
			default:
				throw new ArgumentCountMismatchException("method", "rsplit", args.length, 0, 2);
		}
	}

	public String getName()
	{
		return "rsplit";
	}
}
