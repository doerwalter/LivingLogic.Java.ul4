/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class BoundStringMethodStrip extends BoundMethod<String>
{
	private static final Signature signature = new Signature("strip", "chars", null);

	public BoundStringMethodStrip(String object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static String call(String object)
	{
		return StringUtils.strip(object);
	}

	public static String call(String object, String chars)
	{
		return StringUtils.strip(object, chars);
	}

	public Object callUL4(Object[] args)
	{
		if (args[0] == null)
			return call(object);
		else if (args[0] instanceof String)
			return call(object, (String)args[0]);
		throw new ArgumentTypeMismatchException("{}.strip({})", object, args[0]);
	}
}
