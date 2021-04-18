/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class FunctionSin extends Function
{
	@Override
	public String getNameUL4()
	{
		return "sin";
	}

	private static final Signature signature = new Signature().addPositionalOnly("x");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static double call(double obj)
	{
		return Math.sin(obj);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Number)
			return call(((Number)obj).doubleValue());
		throw new ArgumentTypeMismatchException("sin({!t}) not supported", obj);
	}

	public static final Function function = new FunctionSin();
}
