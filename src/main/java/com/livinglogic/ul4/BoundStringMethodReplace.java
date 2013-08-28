/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringUtils;
import java.util.Map;

public class BoundStringMethodReplace extends BoundMethod<String>
{
	private static Signature signature = new Signature("replace", "old", Signature.required, "new", Signature.required, "count", null);

	public BoundStringMethodReplace(String object)
	{
		super(object);
	}

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

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		if (!(args[0] instanceof String) || !(args[1] instanceof String))
			throw new ArgumentTypeMismatchException("{}.replace({}, {})", object, args[0], args[1]);

		if (args[2] == null)
			return call(object, (String)args[0], (String)args[1]);
		else
			return call(object, (String)args[0], (String)args[1], Utils.toInt(args[2]));
	}
}
