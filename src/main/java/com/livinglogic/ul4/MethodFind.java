/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;

public class MethodFind extends NormalMethod
{
	public String nameUL4()
	{
		return "find";
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

	public static int call(String obj, String sub)
	{
		return obj.indexOf(sub);
	}

	public static int call(List obj, Object sub)
	{
		return obj.indexOf(sub);
	}

	public static int call(String obj, String sub, int start)
	{
		start = Utils.getSliceStartPos(obj.length(), start);
		return obj.indexOf(sub, start);
	}

	public static int call(List obj, Object sub, int start)
	{
		start = Utils.getSliceStartPos(obj.size(), start);
		if (start != 0)
			obj = obj.subList(start, obj.size());
		int pos = obj.indexOf(sub);
		if (pos != -1)
			pos += start;
		return pos;
	}

	public static Object call(Object obj, Object sub, Object start)
	{
		if (obj instanceof String && sub instanceof String)
			return call((String)obj, (String)sub, Utils.toInt(start));
		else if (obj instanceof List)
			return call((List)obj, sub, Utils.toInt(start));
		throw new ArgumentTypeMismatchException("{}.find({}, {})", obj, sub, start);
	}

	public static Object call(String obj, String sub, int start, int end)
	{
		start = Utils.getSliceStartPos(obj.length(), start);
		end = Utils.getSliceEndPos(obj.length(), end);
		int result = obj.indexOf(sub, start);
		if (result + sub.length() > end)
			return -1;
		return result;
	}

	public static int call(List obj, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(obj.size(), start);
		end = Utils.getSliceEndPos(obj.size(), end);
		if (start != 0)
			obj = obj.subList(start, end);
		int pos = obj.indexOf(sub);
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
		throw new ArgumentTypeMismatchException("{}.find({}, {}, {})", obj, sub, start, end);
	}
}
