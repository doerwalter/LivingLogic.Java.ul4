/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;

public class FunctionLast extends Function
{
	public String nameUL4()
	{
		return "last";
	}

	private static final Signature signature = new Signature("iterable", Signature.required, "default", null);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0), args.get(1));
	}

	public static Object call(Object iterable, Object defaultValue)
	{
		Iterator iter = Utils.iterator(iterable);

		Object result = defaultValue;
		for (;iter.hasNext();)
			result = iter.next();
		return result;
	}
}
