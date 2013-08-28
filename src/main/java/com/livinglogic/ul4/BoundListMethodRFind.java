/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.List;

public class BoundListMethodRFind extends BoundMethod<List>
{
	private static Signature signature = new Signature("rfind", "sub", Signature.required, "start", null, "end", null);

	public BoundListMethodRFind(List object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static int call(List object, Object sub)
	{
		return object.lastIndexOf(sub);
	}

	public static int call(List object, Object sub, int start)
	{
		start = Utils.getSliceStartPos(object.size(), start);
		int result = object.lastIndexOf(sub);
		if (result < start)
			return -1;
		return result;
	}

	public static int call(List object, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(object.size(), start);
		end = Utils.getSliceStartPos(object.size(), end);
		object = object.subList(start, end);
		int pos = object.lastIndexOf(sub);
		if (pos != -1)
			pos += start;
		return pos;
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		int startIndex = args[1] != null ? Utils.toInt(args[1]) : 0;
		int endIndex = args[2] != null ? Utils.toInt(args[2]) : object.size();

		return call(object, args[0], startIndex, endIndex);
	}
}
