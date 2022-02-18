/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionFromUL4ON extends Function
{
	@Override
	public String getNameUL4()
	{
		return "fromul4on";
	}

	private static final Signature signature = new Signature().addPositionalOnly("dump");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(args.getString(0));
	}

	public static Object call(String obj)
	{
		return com.livinglogic.ul4on.Utils.loads(obj, null);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("fromul4on({!t}) not supported", obj);
	}

	public static final Function function = new FunctionFromUL4ON();
}
