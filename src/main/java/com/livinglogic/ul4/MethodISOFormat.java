/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodISOFormat implements Method
{
	public Object call(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return Utils.isoformat(obj);
			default:
				throw new ArgumentCountMismatchException("method", "isoformat", args.length, 0);
		}
	}

	public String getName()
	{
		return "isoformat";
	}
}
