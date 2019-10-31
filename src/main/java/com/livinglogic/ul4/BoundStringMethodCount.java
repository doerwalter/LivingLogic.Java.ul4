/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundStringMethodCount extends BoundMethod<String>
{
	public BoundStringMethodCount(String object)
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

	public static int call(String object, String sub)
	{
		return call(object, sub, 0, object.length());
	}

	public static int call(String object, String sub, int start)
	{
		return call(object, sub, start, object.length());
	}

	public static int call(String object, String sub, int start, int end)
	{
		int length = object.length();
		if (start < 0)
			start += length;
		if (end < 0)
			end += length;

		if (sub.length() == 0)
		{
			if (end < 0 || start > length || start > end)
				return 0;
			int result = end - start + 1;
			if (result > length + 1)
				result = length + 1;
			return result;
		}

		start = Utils.getSliceStartPos(length, start);
		end = Utils.getSliceEndPos(length, end);

		int count = 0;
		int lastIndex = start;

		for (;;)
		{
			lastIndex = object.indexOf(sub, lastIndex);
			if (lastIndex == -1)
				break;
			if (lastIndex + sub.length() > end)
				break;
			++count;
			lastIndex += sub.length();
		}
		return count;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		if (args.get(0) instanceof String)
		{
			int startIndex = args.get(1) != null ? Utils.toInt(args.get(1)) : 0;
			int endIndex = args.get(2) != null ? Utils.toInt(args.get(2)) : object.length();
			return call(object, (String)args.get(0), startIndex, endIndex);
		}
		throw new ArgumentTypeMismatchException("{!t}.count({!t}, {!t}, {!t}) not supported", object, args.get(0), args.get(1), args.get(2));
	}
}
