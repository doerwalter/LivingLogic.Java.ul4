/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;

public class FunctionLast extends Function
{
	@Override
	public String getNameUL4()
	{
		return "last";
	}

	private static final Signature signature = new Signature().addPositionalOnly("iterable").addBoth("default", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
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

	public static final Function function = new FunctionLast();
}
