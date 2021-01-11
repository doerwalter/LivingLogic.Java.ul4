/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.List;

public class BoundArrayMethodCount extends BoundMethod<Object[]>
{
	public BoundArrayMethodCount(Object[] object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "count";
	}

	private static final Signature signature = new Signature("sub", Signature.required, "start", null, "end", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static int call(Object[] object, Object sub)
	{
		return call(object, sub, 0, object.length);
	}

	public static int call(Object[] object, Object sub, int start)
	{
		return call(object, sub, start, object.length);
	}

	public static int call(Object[] object, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(object.length, start);
		end = Utils.getSliceEndPos(object.length, end);

		int count = 0;
		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(object[i], sub))
				++count;
		}
		return count;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		int startIndex = args.get(1) != null ? Utils.toInt(args.get(1)) : 0;
		int endIndex = args.get(2) != null ? Utils.toInt(args.get(2)) : object.length;

		return call(object, args.get(0), startIndex, endIndex);
	}
}
