/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;

public class FunctionFirst extends Function
{
	@Override
	public String getNameUL4()
	{
		return "first";
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
		return call(context, args.get(0), args.get(1));
	}

	public static Object call(EvaluationContext context, Object iterable, Object defaultValue)
	{
		Iterator iter = Utils.iterator(iterable);

		for (;iter.hasNext();)
			return iter.next();
		return defaultValue;
	}

	public static final Function function = new FunctionFirst();
}
