/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;

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

	public static int call(List obj, Object search)
	{
		return obj.lastIndexOf(search);
	}

	public static int call(Object obj, Object search)
	{
		if (obj instanceof String && search instanceof String)
			return call((String)obj, (String)search);
		else if (obj instanceof List)
			return call((List)obj, search);
		throw new ArgumentTypeMismatchException("{}.rfind({})", obj, search);
	}

	public static int call(String obj, String search, int start)
	{
		start = Utils.getSliceStartPos(obj.length(), start);
		int result = obj.lastIndexOf(search);
		if (result < start)
			return -1;
		return result;
	}

	public static int call(List obj, Object search, int start)
	{
		start = Utils.getSliceStartPos(obj.size(), start);
		int result = obj.lastIndexOf(search);
		if (result < start)
			return -1;
		return result;
	}

	public static int call(Object obj, Object search, Object start)
	{
		if (obj instanceof String && search instanceof String)
			return call((String)obj, (String)search, Utils.toInt(start));
		else if (obj instanceof List)
			return call((List)obj, search, Utils.toInt(start));
		throw new ArgumentTypeMismatchException("{}.rfind({}, {})", obj, search, start);
	}

	public static int call(String obj, String search, int start, int end)
	{
		start = Utils.getSliceStartPos(obj.length(), start);
		end = Utils.getSliceStartPos(obj.length(), end);
		end -= search.length();
		if (end < 0)
			return -1;
		int result = obj.lastIndexOf(search, end);
		if (result < start)
			return -1;
		return result;
	}

	public static int call(List obj, Object search, int start, int end)
	{
		start = Utils.getSliceStartPos(obj.size(), start);
		end = Utils.getSliceStartPos(obj.size(), end);
		obj = obj.subList(start, end);
		int pos = obj.lastIndexOf(search);
		if (pos != -1)
			pos += start;
		return pos;
	}

	public static int call(Object obj, Object search, Object start, Object end)
	{
		if (obj instanceof String && search instanceof String)
			return call((String)obj, (String)search, Utils.toInt(start), Utils.toInt(end));
		else if (obj instanceof List)
			return call((List)obj, search, Utils.toInt(start), Utils.toInt(end));
		throw new ArgumentTypeMismatchException("{}.rfind({}, {}, {})", obj, search, start, end);
	}
}
