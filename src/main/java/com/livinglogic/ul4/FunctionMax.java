/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class FunctionMax implements UL4Callable
{
	public String getName()
	{
		return "max";
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		if (kwargs.size() != 0)
			throw new KeywordArgumentsNotSupportedException(this.getName());
		return args.length == 0 ? call() : call(args);
	}

	public static Object call()
	{
		throw new MissingArgumentException("max", "iterable", 0);
	}

	public static Object call(Object[] objs)
	{
		Iterator iter = Utils.iterator(objs.length == 1 ? objs[0] : objs);

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
