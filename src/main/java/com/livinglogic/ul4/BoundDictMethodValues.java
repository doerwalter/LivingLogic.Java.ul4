/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.Map;

import java.util.List;
import java.util.Map;

public class BoundDictMethodValues extends BoundMethod<Map>
{
	private static Signature signature = new Signature("values");

	public BoundDictMethodValues(Map object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(Map object)
	{
		return object.values();
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		return call(object);
	}
}
