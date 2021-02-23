/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundListMethodRFind extends BoundMethod<List>
{
	public BoundListMethodRFind(List object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "rfind";
	}

	private static final Signature signature = new Signature("sub", Signature.required, "start", null, "end", null);

	@Override
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
		end = Utils.getSliceEndPos(object.size(), end);
		object = object.subList(start, end);
		int pos = object.lastIndexOf(sub);
		if (pos != -1)
			pos += start;
		return pos;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		int startIndex = args.get(1) != null ? Utils.toInt(args.get(1)) : 0;
		int endIndex = args.get(2) != null ? Utils.toInt(args.get(2)) : object.size();

		return call(object, args.get(0), startIndex, endIndex);
	}
}
