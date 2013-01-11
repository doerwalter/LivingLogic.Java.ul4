/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;

public class MethodGet extends NormalMethod
{
	public String getName()
	{
		return "get";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("key");
		signature.add("default", null);
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj, args[0], args[1]);
	}

	public static Object call(Object obj, Object key)
	{
		if (obj instanceof Map)
			return ((Map)obj).get(key);
		throw new ArgumentTypeMismatchException("{}.get({})", obj, key);
	}

	public static Object call(Object obj, Object key, Object defaultValue)
	{
		if (obj instanceof Map)
		{
			Object result = ((Map)obj).get(key);
			if (result == null && !((Map)obj).containsKey(key))
				result = defaultValue;
			return result;
		}
		throw new ArgumentTypeMismatchException("{}.get({}, {})", obj, key, defaultValue);
	}
}
