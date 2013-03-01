/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringUtils;

public class MethodLStrip extends NormalMethod
{
	public String nameUL4()
	{
		return "lstrip";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("chars", null);
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj, args[0]);
	}

	public static String call(String obj)
	{
		return StringUtils.stripStart(obj, null);
	}

	public static String call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("{}.lstrip()", obj);
	}

	public static String call(String obj, String stripChars)
	{
		return StringUtils.stripStart(obj, stripChars);
	}

	public static String call(Object obj, Object stripChars)
	{
		if (obj instanceof String)
		{
			if (stripChars == null)
				return call((String)obj);
			else if (stripChars instanceof String)
				return call((String)obj, (String)stripChars);
		}
		throw new ArgumentTypeMismatchException("{}.lstrip({})", obj, stripChars);
	}
}
