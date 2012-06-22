/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import org.apache.commons.lang.StringUtils;

public class MethodRStrip implements Method
{
	public String getName()
	{
		return "rstrip";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(obj);
			case 1:
				return call(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "rstrip", args.length, 0, 1);
		}
	}

	public static String call(String obj)
	{
		return StringUtils.stripEnd(obj, null);
	}

	public static String call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("{}.rstrip()", obj);
	}

	public static String call(String obj, String stripChars)
	{
		return StringUtils.stripEnd(obj, stripChars);
	}

	public static Object call(Object obj, Object stripChars)
	{
		if (obj instanceof String && stripChars instanceof String)
			return call((String)obj, (String)stripChars);
		throw new ArgumentTypeMismatchException("{}.rstrip({})", obj, stripChars);
	}
}
