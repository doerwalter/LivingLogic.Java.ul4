/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundUL4GetItemStringMethodGet extends BoundMethod<UL4GetItemString>
{
	private static final Signature signature = new Signature("get", "key", Signature.required, "default", null);

	public BoundUL4GetItemStringMethodGet(UL4GetItemString object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(UL4GetItemString object, Object key, Object defaultValue)
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

	public Object callUL4(Object[] args)
	{
		return call(object, args[0], args[1]);
	}
}
