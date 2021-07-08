/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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

	@Override
	public String getNameUL4()
	{
		return "get";
	}

	private static final Signature signature = new Signature().addPositionalOnly("key").addPositionalOnly("default", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(EvaluationContext context, Map object, Object key, Object defaultValue)
	{
		Object result = object.get(key);
		if (result == null && !object.containsKey(key))
			return defaultValue;
		return result;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object, args.get(0), args.get(1));
	}
}
