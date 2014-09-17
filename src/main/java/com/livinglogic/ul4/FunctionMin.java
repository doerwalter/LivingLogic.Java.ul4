/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;

public class FunctionMin extends Function
{
	public String nameUL4()
	{
		return "min";
	}

	private static final Signature signature = new Signature("args", Signature.remainingArguments);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(List<Object> args)
	{
		List<Object> argList = (List<Object>)args.get(0);
		return (argList.size() == 0) ? call() : call(argList);
	}

	public static Object call()
	{
		throw new MissingArgumentException("min", "args", 0);
	}

	public static Object call(List<Object> objs)
	{
		Iterator iter = Utils.iterator(objs.size() == 1 ? objs.get(0) : objs);

		Object minValue = null;
		boolean first = true;

		for (;iter.hasNext();)
		{
			Object testValue = iter.next();
			if (first || LTAST.call(testValue, minValue))
				minValue = testValue;
			first = false;
		}
		if (first)
			throw new UnsupportedOperationException("min() arg is an empty sequence!");
		return minValue;
	}
}
