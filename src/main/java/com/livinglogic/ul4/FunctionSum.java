/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
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

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"iterable", Signature.required,
			"start", 0
		);
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
			sum = Add.call(sum, iter.next());
		}
		return sum;
	}
}
