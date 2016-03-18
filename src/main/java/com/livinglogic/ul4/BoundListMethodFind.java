/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.List;

public class BoundListMethodFind extends BoundMethod<List>
{
	public BoundListMethodFind(List object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "list.find";
	}

	private static final Signature signature = new Signature("sub", Signature.required, "start", null, "end", null);

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

	public Object evaluate(BoundArguments args)
	{
		int startIndex = args.get(1) != null ? Utils.toInt(args.get(1)) : 0;
		int endIndex = args.get(2) != null ? Utils.toInt(args.get(2)) : object.size();

		return call(object, args.get(0), startIndex, endIndex);
	}
}
