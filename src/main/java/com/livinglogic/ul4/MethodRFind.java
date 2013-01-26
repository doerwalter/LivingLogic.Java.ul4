/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;

public class MethodRFind extends NormalMethod
{
	public String nameUL4()
	{
		return "rfind";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("sub");
		signature.add("start", null);
		signature.add("end", null);
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj, args[0], args[1], args[2]);
	}

	public static int call(String obj, String search)
	{
		return obj.lastIndexOf(search);
	}

	public static int call(List obj, Object search)
	{
		return obj.lastIndexOf(search);
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

	public static Object call(Object obj, Object sub, Object start, Object end)
	{
		if (obj instanceof String && sub instanceof String)
		{
			int startIndex = start != null ? Utils.toInt(start) : 0;
			int endIndex = end != null ? Utils.toInt(end) : ((String)obj).length();
			return call((String)obj, (String)sub, startIndex, endIndex);
		}
		else if (obj instanceof List)
		{
			int startIndex = start != null ? Utils.toInt(start) : 0;
			int endIndex = end != null ? Utils.toInt(end) : ((List)obj).size();
			return call((List)obj, sub, startIndex, endIndex);
		}
		throw new ArgumentTypeMismatchException("{}.rfind({}, {}, {})", obj, sub, start, end);
	}
}
