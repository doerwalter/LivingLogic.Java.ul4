/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundListMethodInsert extends BoundMethod<List>
{
	private static final Signature signature = new Signature("insert", "pos", Signature.required, "items", Signature.remainingArguments);

	public BoundListMethodInsert(List object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static void call(List object, int pos, List<Object> items)
	{
		if (pos < 0)
			pos += object.size();
		object.addAll(pos, items);
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		call(object, Utils.toInt(args[0]), (List<Object>)args[1]);
		return null;
	}
}
