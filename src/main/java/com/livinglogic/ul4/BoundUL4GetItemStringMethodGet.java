/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundUL4GetItemStringMethodGet extends BoundMethod<UL4GetItemString>
{
	public BoundUL4GetItemStringMethodGet(UL4GetItemString object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "object.get";
	}

	private static final Signature signature = new Signature("key", Signature.required, "default", null);

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

	public Object evaluate(List<Object> args)
	{
		return call(object, args.get(0), args.get(1));
	}
}
