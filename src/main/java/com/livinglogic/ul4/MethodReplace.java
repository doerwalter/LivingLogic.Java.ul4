/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

public class MethodReplace extends NormalMethod
{
	public String nameUL4()
	{
		return "replace";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("old");
		signature.add("new");
		signature.add("count", null);
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj, args[0], args[1], args[2]);
	}

	public static String call(String obj, String search, String replace)
	{
		return StringUtils.replace(obj, search, replace);
	}

	public static String call(String obj, String search, String replace, int count)
	{
		return StringUtils.replace(obj, search, replace, count);
	}

	public static String call(Object obj, Object search, Object replace, Object count)
	{
		if (obj instanceof String && search instanceof String && replace instanceof String)
		{
			if (count == null)
				return call((String)obj, (String)search, (String)replace);
			else
				return call((String)obj, (String)search, (String)replace, Utils.toInt(count));
		}
		throw new ArgumentTypeMismatchException("{}.replace({}, {})", obj, search, replace);
	}
}
