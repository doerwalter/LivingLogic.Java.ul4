/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class BoundStringMethodReplace extends BoundMethod<String>
{
	public BoundStringMethodReplace(String object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "str.replace";
	}

	private static final Signature signature = new Signature("old", Signature.required, "new", Signature.required, "count", null);

	public Signature getSignature()
	{
		return signature;
	}

	public static String call(String object, String search, String replace)
	{
		return object.replace(search, replace);
	}

	public static String call(String object, String search, String replace, int count)
	{
		return StringUtils.replace(object, search, replace, count);
	}

	public Object evaluate(BoundArguments args)
	{
		Object arg1 = args.get(0);
		Object arg2 = args.get(1);
		Object arg3 = args.get(2);

		if (!(arg1 instanceof String) || !(arg2 instanceof String))
			throw new ArgumentTypeMismatchException("{}.replace({}, {})", object, arg1, arg2);

		if (arg3 == null)
			return call(object, (String)arg1, (String)arg2);
		else
			return call(object, (String)arg1, (String)arg2, Utils.toInt(arg3));
	}
}
