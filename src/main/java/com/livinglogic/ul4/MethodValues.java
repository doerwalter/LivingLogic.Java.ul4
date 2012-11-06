/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class MethodValues implements Method
{
	public String getName()
	{
		return "values";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(obj);
			default:
				throw new ArgumentCountMismatchException("method", "values", args.length, 0);
		}
	}

	public static Object call(Map obj)
	{
		return obj.values();
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Map)
			return call((Map)obj);
		throw new ArgumentTypeMismatchException("{}.values()", obj);
	}
}
