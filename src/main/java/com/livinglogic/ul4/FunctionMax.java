/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class FunctionMax extends Function
{
	public String nameUL4()
	{
		return "max";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"args", Signature.remainingArguments
		);
	}

	public Object evaluate(Object[] args)
	{
		List<Object> argList = (List<Object>)args[0];
		return (argList.size() == 0) ? call() : call(argList);
	}

	public static Object call()
	{
		throw new MissingArgumentException("max", "args", 0);
	}

	public static Object call(List<Object> objs)
	{
		Iterator iter = Utils.iterator(objs.size() == 1 ? objs.get(0) : objs);

		Object maxValue = null;
		boolean first = true;

		for (;iter.hasNext();)
		{
			Object testValue = iter.next();
			if (first || GT.call(testValue, maxValue))
				maxValue = testValue;
			first = false;
		}
		if (first)
			throw new UnsupportedOperationException("max() arg is an empty sequence!");
		return maxValue;
	}
}
