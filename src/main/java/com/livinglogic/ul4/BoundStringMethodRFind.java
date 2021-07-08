/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundStringMethodRFind extends BoundMethod<String>
{
	public BoundStringMethodRFind(String object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "rfind";
	}

	private static final Signature signature = new Signature().addPositionalOnly("sub").addPositionalOnly("start", null).addPositionalOnly("end", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static int call(EvaluationContext context, String object, String sub)
	{
		return object.lastIndexOf(sub);
	}

	public static int call(EvaluationContext context, String object, String sub, int start)
	{
		start = Utils.getSliceStartPos(object.length(), start);
		int result = object.lastIndexOf(sub);
		if (result < start)
			return -1;
		return result;
	}

	public static int call(EvaluationContext context, String object, String sub, int start, int end)
	{
		start = Utils.getSliceStartPos(object.length(), start);
		end = Utils.getSliceStartPos(object.length(), end);
		end -= sub.length();
		if (end < 0)
			return -1;
		int result = object.lastIndexOf(sub, end);
		if (result < start)
			return -1;
		return result;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		if (args.get(0) instanceof String)
		{
			int startIndex = args.get(1) != null ? Utils.toInt(args.get(1)) : 0;
			int endIndex = args.get(2) != null ? Utils.toInt(args.get(2)) : object.length();
			return call(context, object, (String)args.get(0), startIndex, endIndex);
		}
		throw new ArgumentTypeMismatchException("{!t}.rfind({!t}, {!t}, {!t}) not supported", object, args.get(0), args.get(1), args.get(2));
	}
}
