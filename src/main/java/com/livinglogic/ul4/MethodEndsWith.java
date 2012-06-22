/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodEndsWith implements Method
{
	public String getName()
	{
		return "endswith";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 1:
				return call(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "endswith", args.length, 1);
		}
	}

	public static boolean call(String obj, String arg)
	{
		return obj.endsWith(arg);
	}

	public static boolean call(Object obj, Object arg)
	{
		if (obj instanceof String && arg instanceof String)
			return call((String)obj, (String)arg);
		throw new ArgumentTypeMismatchException("{}.endswith({})", obj, arg);
	}

}
