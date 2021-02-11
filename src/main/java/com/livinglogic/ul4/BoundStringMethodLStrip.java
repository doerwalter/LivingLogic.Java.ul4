/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class BoundStringMethodLStrip extends BoundMethod<String>
{
	public BoundStringMethodLStrip(String object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "lstrip";
	}

	private static final Signature signature = new Signature("chars", null);

	@Override
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

	@Override
	public Object evaluate(BoundArguments args)
	{
		Object arg = args.get(0);

		if (arg == null)
			return call(object);
		else if (arg instanceof String)
			return call(object, (String)arg);
		throw new ArgumentTypeMismatchException("{!t}.lstrip({!t}) not supported", object, arg);
	}
}
