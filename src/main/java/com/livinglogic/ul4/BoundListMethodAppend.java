/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundListMethodAppend extends BoundMethod<List>
{
	private static Signature signature = new Signature("append", "items", Signature.remainingArguments);

	public BoundListMethodAppend(List object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static void call(List object, List<Object> items)
	{
		object.addAll(items);
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		call(object, (List<Object>)args[0]);
		return null;
	}
}
