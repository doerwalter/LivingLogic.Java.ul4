/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.List;

public class BoundArrayMethodFind extends BoundMethod<Object[]>
{
	public BoundArrayMethodFind(Object[] object)
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

	public static int call(Object[] object, Object sub)
	{
		int start = 0;
		int end = object.length;

		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(object[i], sub))
				return i;
		}
		return -1;
	}

	public static int call(Object[] object, Object sub, int start)
	{
		start = Utils.getSliceStartPos(object.length, start);
		int end = object.length;

		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(object[i], sub))
				return i;
		}
		return -1;
	}

	public static int call(Object[] object, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(object.length, start);
		end = Utils.getSliceEndPos(object.length, end);

		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(object[i], sub))
				return i;
		}
		return -1;
	}

	public Object evaluate(BoundArguments args)
	{
		int startIndex = args.get(1) != null ? Utils.toInt(args.get(1)) : 0;
		int endIndex = args.get(2) != null ? Utils.toInt(args.get(2)) : object.length;

		return call(object, args.get(0), startIndex, endIndex);
	}
}
