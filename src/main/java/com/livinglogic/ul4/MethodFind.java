/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodFind implements Method
{
	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 1:
				return Utils.find(obj, args[0]);
			case 2:
				return Utils.find(obj, args[0], args[1]);
			case 3:
				return Utils.find(obj, args[0], args[1], args[2]);
			default:
				throw new ArgumentCountMismatchException("method", "find", args.length, 1, 3);
		}
	}

	public String getName()
	{
		return "find";
	}
}
