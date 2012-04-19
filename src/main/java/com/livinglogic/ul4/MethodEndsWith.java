/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodEndsWith implements Method
{
	public Object call(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 1:
				return Utils.endswith(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "endswith", args.length, 1);
		}
	}

	public String getName()
	{
		return "endswith";
	}
}
