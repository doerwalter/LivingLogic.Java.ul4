/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundDictMethodGet extends BoundMethod<Map>
{
	public BoundDictMethodGet(Map object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "dict.get";
	}

	private static final Signature signature = new Signature("key", Signature.required, "default", null);

	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(Map object, Object key, Object defaultValue)
	{
		Object result = object.get(key);
		if (result == null && !object.containsKey(key))
			result = defaultValue;
		return result;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object, args.get(0), args.get(1));
	}
}
