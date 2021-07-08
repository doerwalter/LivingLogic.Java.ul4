/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.List;

public class BoundListMethodCount extends BoundMethod<List>
{
	public BoundListMethodCount(List object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "count";
	}

	private static final Signature signature = new Signature().addPositionalOnly("sub").addPositionalOnly("start", null).addPositionalOnly("end", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static int call(EvaluationContext context, List object, Object sub)
	{
		return call(context, object, sub, 0, object.size());
	}

	public static int call(EvaluationContext context, List object, Object sub, int start)
	{
		return call(context, object, sub, start, object.size());
	}

	public static int call(EvaluationContext context, List object, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(object.size(), start);
		end = Utils.getSliceEndPos(object.size(), end);

		int count = 0;
		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(context, object.get(i), sub))
				++count;
		}
		return count;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		int startIndex = args.get(1) != null ? Utils.toInt(args.get(1)) : 0;
		int endIndex = args.get(2) != null ? Utils.toInt(args.get(2)) : object.size();

		return call(context, object, args.get(0), startIndex, endIndex);
	}
}
