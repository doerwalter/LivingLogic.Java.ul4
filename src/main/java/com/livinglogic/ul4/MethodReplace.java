/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

public class MethodReplace implements Method
{
	public String getName()
	{
		return "replace";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 2:
				return call(obj, args[0], args[1]);
			default:
				throw new ArgumentCountMismatchException("method", "replace", args.length, 2);
		}
	}

	public static String call(String obj, String search, String replace)
	{
		return StringUtils.replace(obj, search, replace);
	}

	public static String call(Object obj, String search, String replace)
	{
		if (obj instanceof String)
			return call((String)obj, search, replace);
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".replace(" + Utils.objectType(search) + ", " + Utils.objectType(replace) + ") not supported!");
	}

	public static String call(Object obj, Object search, Object replace)
	{
		if (search instanceof String && replace instanceof String)
			return call(obj, (String)search, (String)replace);
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".replace(" + Utils.objectType(search) + ", " + Utils.objectType(replace) + ") not supported!");
	}
}
