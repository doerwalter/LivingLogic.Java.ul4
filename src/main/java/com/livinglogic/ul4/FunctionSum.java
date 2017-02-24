/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;

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

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0), args.get(1));
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
