/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodGet implements Method
{
	public Object call(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 1:
				return Utils.get(obj, args[0]);
			case 2:
				return Utils.get(obj, args[0], args[1]);
			default:
				throw new ArgumentCountMismatchException("method", "get", args.length, 1, 2);
		}
	}

	public String getName()
	{
		return "get";
	}
}
