/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundStringMethodFind extends BoundMethod<String>
{
	public BoundStringMethodFind(String object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "str.find";
	}

	private static final Signature signature = new Signature("sub", Signature.required, "start", null, "end", null);

	public Signature getSignature()
	{
		return signature;
	}

	public static int call(String object, String sub)
	{
		return object.indexOf(sub);
	}

	public static int call(String object, String sub, int start)
	{
		start = Utils.getSliceStartPos(object.length(), start);
		return object.indexOf(sub, start);
	}

	public static int call(String object, String sub, int start, int end)
	{
		start = Utils.getSliceStartPos(object.length(), start);
		end = Utils.getSliceEndPos(object.length(), end);
		int result = object.indexOf(sub, start);
		if (result + sub.length() > end)
			return -1;
		return result;
	}

	public Object evaluate(Object[] args)
	{
		if (args[0] instanceof String)
		{
			int startIndex = args[1] != null ? Utils.toInt(args[1]) : 0;
			int endIndex = args[2] != null ? Utils.toInt(args[2]) : object.length();
			return call(object, (String)args[0], startIndex, endIndex);
		}
		throw new ArgumentTypeMismatchException("{}.find({}, {}, {})", object, args[0], args[1], args[2]);
	}
}
