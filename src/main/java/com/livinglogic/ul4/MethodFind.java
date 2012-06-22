/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodFind implements Method
{
	public String getName()
	{
		return "find";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 1:
				return call(obj, args[0]);
			case 2:
				return call(obj, args[0], args[1]);
			case 3:
				return call(obj, args[0], args[1], args[2]);
			default:
				throw new ArgumentCountMismatchException("method", "find", args.length, 1, 3);
		}
	}

	public static int call(String obj, String sub)
	{
		return obj.indexOf(sub);
	}

	public static int call(Object obj, Object sub)
	{
		if (obj instanceof String && sub instanceof String)
			return call((String)obj, (String)sub);
		throw new ArgumentTypeMismatchException("{}.find({})", obj, sub);
	}

	public static int call(String obj, String sub, int start)
	{
		return obj.indexOf(sub, start);
	}

	public static Object call(Object obj, Object sub, Object start)
	{
		if (obj instanceof String && sub instanceof String)
			return call((String)obj, (String)sub, Utils.toInt(start));
		throw new ArgumentTypeMismatchException("{}.find({}, {})", obj, sub, start);
	}

	public static Object call(String obj, String sub, int start, int end)
	{
		int result = obj.indexOf(sub, start);
		if (result + sub.length() > end)
			return -1;
		return result;
	}

	public static Object call(Object obj, Object sub, Object start, Object end)
	{
		if (obj instanceof String && sub instanceof String)
			return call((String)obj, (String)sub, Utils.toInt(start), Utils.toInt(end));
		throw new ArgumentTypeMismatchException("{}.find({}, {}, {})", obj, sub, start, end);
	}
}
