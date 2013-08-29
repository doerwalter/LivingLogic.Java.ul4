/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class BoundStringMethodLStrip extends BoundMethod<String>
{
	private static final Signature signature = new Signature("lstrip", "chars", null);

	public BoundStringMethodLStrip(String object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static String call(String object)
	{
		return StringUtils.stripStart(object, null);
	}

	public static String call(String object, String chars)
	{
		return StringUtils.stripStart(object, chars);
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		if (args[0] == null)
			return call(object);
		else if (args[0] instanceof String)
			return call(object, (String)args[0]);
		throw new ArgumentTypeMismatchException("{}.lstrip({})", object, args[0]);
	}
}
