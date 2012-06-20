/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodStartsWith implements Method
{
	public String getName()
	{
		return "startswith";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 1:
				return call(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "startswith", args.length, 1);
		}
	}

	public static Object call(String obj, String prefix)
	{
		return obj.startsWith(prefix);
	}

	public static Object call(Object obj, Object prefix)
	{
		if (obj instanceof String && prefix instanceof String)
			return call((String)obj, (String)prefix);
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".startswith(" + Utils.objectType(prefix) + ") not supported!");
	}
}
