/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundDictMethodGet extends BoundMethod<Map>
{
	private static final Signature signature = new Signature("get", "key", Signature.required, "default", null);

	public BoundDictMethodGet(Map object)
	{
		super(object);
	}

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

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		return call(object, args[0], args[1]);
	}
}
