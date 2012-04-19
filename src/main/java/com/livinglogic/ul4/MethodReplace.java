/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodReplace implements Method
{
	public Object call(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 2:
				return Utils.replace(obj, args[0], args[1]);
			default:
				throw new ArgumentCountMismatchException("method", "replace", args.length, 2);
		}
	}

	public String getName()
	{
		return "replace";
	}
}
