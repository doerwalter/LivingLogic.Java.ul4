/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundListMethodPop extends BoundMethod<List>
{
	private static Signature signature = new Signature("pop", "pos", -1);

	public BoundListMethodPop(List object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(List obj, int pos)
	{
		if (pos < 0)
			pos += obj.size();
		return obj.remove(pos);
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		return call(object, Utils.toInt(args[0]));
	}
}
