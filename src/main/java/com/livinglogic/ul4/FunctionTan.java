/*
** Copyright 2021-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class FunctionTan extends Function
{
	@Override
	public String getNameUL4()
	{
		return "tan";
	}

	private static final Signature signature = new Signature().addPositionalOnly("x");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(args.get(0));
	}

	public static double call(double obj)
	{
		return Math.tan(obj);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Number)
			return call(((Number)obj).doubleValue());
		throw new ArgumentTypeMismatchException("tan({!t}) not supported", obj);
	}

	public static final Function function = new FunctionTan();
}
