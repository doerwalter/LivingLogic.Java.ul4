/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.util.Collection;
import java.util.Collections;

public class FunctionMax implements Function
{
	public String getName()
	{
		return "max";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		return call(args);
	}

	public static Object call(Object ... objs)
	{
		if (objs.length == 0)
			throw new ArgumentCountMismatchException("function", "max", objs.length, 1, -1);

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
			throw new UnsupportedOperationException("max() sequence is empty!");
		return maxValue;
	}
}
