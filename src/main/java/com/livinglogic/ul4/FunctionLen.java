/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Collection;
import java.util.Map;

public class FunctionLen implements Function
{
	public String getName()
	{
		return "len";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "len", args.length, 1);
	}

	public static int call(String obj)
	{
		return obj.length();
	}

	public static int call(UL4Len obj)
	{
		return obj.lenUL4();
	}

	public static int call(Collection obj)
	{
		return obj.size();
	}

	public static int call(Map obj)
	{
		return obj.size();
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		else if (obj instanceof UL4Len)
			return call((Map)obj);
		else if (obj instanceof Collection)
			return call((Collection)obj);
		else if (obj instanceof Map)
			return call((Map)obj);
		throw new ArgumentTypeMismatchException("len({})", obj);
	}
}
