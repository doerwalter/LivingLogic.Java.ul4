/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.List;

public class BoundListMethodFind extends BoundMethod<List>
{
	private static Signature signature = new Signature("find", "sub", Signature.required, "start", null, "end", null);

	public BoundListMethodFind(List object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static int call(List object, Object sub)
	{
		return object.indexOf(sub);
	}

	public static int call(List object, Object sub, int start)
	{
		start = Utils.getSliceStartPos(object.size(), start);
		if (start != 0)
			object = object.subList(start, object.size());
		int pos = object.indexOf(sub);
		if (pos != -1)
			pos += start;
		return pos;
	}

	public static int call(List object, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(object.size(), start);
		end = Utils.getSliceEndPos(object.size(), end);
		if (start != 0)
			object = object.subList(start, end);
		int pos = object.indexOf(sub);
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
