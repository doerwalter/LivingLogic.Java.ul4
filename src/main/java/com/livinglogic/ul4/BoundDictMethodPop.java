/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


public class BoundDictMethodPop extends BoundMethod<Map>
{
	public BoundDictMethodPop(Map object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "pop";
	}

	private static final Signature signature = new Signature().addPositionalOnly("key").addPositionalOnly("default", Signature.noValue);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(EvaluationContext context, Map object, Object key)
	{
		Object value = object.get(key);
		if (value == null && !object.containsKey(key))
			throw new KeyException(key);
		object.remove(key);
		return value;
	}

	public static Object call(EvaluationContext context, Map object, Object key, Object defaultValue)
	{
		Object value = object.get(key);
		if (value == null && !object.containsKey(key))
			return defaultValue;
		object.remove(key);
		return value;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		if (args.get(1) == Signature.noValue)
			return call(context, object, args.get(0));
		else
			return call(context, object, args.get(0), args.get(1));
	}
}
