/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundArrayMethodRFind extends BoundMethod<Object[]>
{
	public BoundArrayMethodRFind(Object[] object)
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

	public static int call(Object[] object, Object sub)
	{
		int start = 0;
		int end = object.length;

		for (int i = end-1; i >= start; --i)
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

		for (int i = end-1; i >= start; --i)
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

		for (int i = end-1; i >= start; --i)
		{
			if (EQAST.call(object[i], sub))
				return i;
		}
		return -1;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		int startIndex = args.get(1) != null ? Utils.toInt(args.get(1)) : 0;
		int endIndex = args.get(2) != null ? Utils.toInt(args.get(2)) : object.length;

		return call(object, args.get(0), startIndex, endIndex);
	}
}
