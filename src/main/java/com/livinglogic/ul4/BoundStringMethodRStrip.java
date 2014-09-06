/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

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

	public Object evaluate(Object[] args)
	{
		if (args[0] == null)
			return call(object);
		else if (args[0] instanceof String)
			return call(object, (String)args[0]);
		throw new ArgumentTypeMismatchException("{}.rstrip({})", object, args[0]);
	}
}
