/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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
	private static Object noValue = new Object();

	public BoundDictMethodPop(Map object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "pop";
	}

	private static final Signature signature = new Signature("key", Signature.required, "default", noValue);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(Map object, Object key)
	{
		Object value = object.get(key);
		if (value == null && !object.containsKey(key))
			throw new KeyException(key);
		object.remove(key);
		return value;
	}

	public static Object call(Map object, Object key, Object defaultValue)
	{
		Object value = object.get(key);
		if (value == null && !object.containsKey(key))
			return defaultValue;
		object.remove(key);
		return value;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		if (args.get(1) == noValue)
			return call(object, args.get(0));
		else
			return call(object, args.get(0), args.get(1));
	}
}
