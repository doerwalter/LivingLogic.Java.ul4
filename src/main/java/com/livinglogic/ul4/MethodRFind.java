/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodRFind implements Method
{
	public String getName()
	{
		return "rfind";
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
				throw new ArgumentCountMismatchException("method", "rfind", args.length, 1, 3);
		}
	}

	public static int call(String obj, String search)
	{
		return obj.lastIndexOf(search);
	}

	public static int call(Object obj, Object search)
	{
		if (obj instanceof String && search instanceof String)
			return call((String)obj, (String)search);
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".rfind(" + Utils.objectType(search) + ") not supported!");
	}

	public static int call(String obj, String search, int startIndex)
	{
		int result = obj.lastIndexOf(search);
		if (result < startIndex)
			return -1;
		return result;
	}

	public static int call(Object obj, Object search, Object startIndex)
	{
		if (obj instanceof String && search instanceof String)
			return call((String)obj, (String)search, Utils.toInt(startIndex));
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".rfind(" + Utils.objectType(search) + ", " + Utils.objectType(startIndex) + ") not supported!");
	}

	public static int call(String obj, String search, int startIndex, int endIndex)
	{
		endIndex -= search.length();
		if (endIndex < 0)
			return -1;
		int result = obj.lastIndexOf(search, endIndex);
		if (result < startIndex)
			return -1;
		return result;
	}

	public static int call(Object obj, Object search, Object startIndex, Object endIndex)
	{
		if (obj instanceof String && search instanceof String)
			return call((String)obj, (String)search, Utils.toInt(startIndex), Utils.toInt(endIndex));
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".rfind(" + Utils.objectType(search) + ", " + Utils.objectType(startIndex) + ", " + Utils.objectType(endIndex) + ") not supported!");
	}

}
