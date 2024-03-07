/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;

public class FunctionSum extends Function
{
	@Override
	public String getNameUL4()
	{
		return "sum";
	}

	private static final Signature signature = new Signature().addPositionalOnly("iterable").addBoth("start", 0);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0), args.get(1));
	}

	public static Object call(EvaluationContext context, Object iterable, Object start)
	{
		Iterator iter = Utils.iterator(iterable);

		Object sum = start;

		for (;iter.hasNext();)
		{
			sum = AddAST.call(context, sum, iter.next());
		}
		return sum;
	}

	public static final Function function = new FunctionSum();
}
