/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class BoundStringMethodRStrip extends BoundMethod<String>
{
	public BoundStringMethodRStrip(String object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "str.rstrip";
	}

	private static final Signature signature = new Signature("chars", null);

	public Signature getSignature()
	{
		return signature;
	}

	public static String call(String object)
	{
		return StringUtils.stripEnd(object, null);
	}

	public static String call(String object, String chars)
	{
		return StringUtils.stripEnd(object, chars);
	}

	public Object evaluate(BoundArguments args)
	{
		Object arg = args.get(0);

		if (arg == null)
			return call(object);
		else if (arg instanceof String)
			return call(object, (String)arg);
		throw new ArgumentTypeMismatchException("{}.rstrip({})", object, arg);
	}
}
