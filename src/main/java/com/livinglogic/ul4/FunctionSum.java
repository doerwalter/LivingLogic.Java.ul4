/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.List;

public class FunctionSum extends Function
{
	public String nameUL4()
	{
		return "sum";
	}

	private static final Signature signature = new Signature("iterable", Signature.required, "start", 0);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0], args[1]);
	}

	public static Object call(Object iterable, Object start)
	{
		Iterator iter = Utils.iterator(iterable);

		Object sum = start;

		for (;iter.hasNext();)
		{
			sum = AddAST.call(sum, iter.next());
		}
		return sum;
	}
}
