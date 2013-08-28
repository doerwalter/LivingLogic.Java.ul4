/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundUL4AttributesMethodGet extends BoundMethod<UL4Attributes>
{
	private static Signature signature = new Signature("get", "key", Signature.required, "default", null);

	public BoundUL4AttributesMethodGet(UL4Attributes object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(UL4Attributes object, Object key, Object defaultValue)
	{
		if (key instanceof String)
		{
			Object result = object.getItemStringUL4((String)key);
			if (result instanceof Undefined)
				result = defaultValue;
			return result;
		}
		else
			return defaultValue;
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		return call(object, args[0], args[1]);
	}
}
