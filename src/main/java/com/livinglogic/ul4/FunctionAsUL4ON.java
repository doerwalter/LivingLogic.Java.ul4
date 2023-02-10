/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionAsUL4ON extends Function
{
	@Override
	public String getNameUL4()
	{
		return "asul4on";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj").addBoth("indent", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments arguments)
	{
		Object obj = arguments.get(0);
		String indent = arguments.getString(1, null);

		return call(context, obj, indent);
	}

	public static String call(EvaluationContext context, Object obj)
	{
		return com.livinglogic.ul4on.Utils.dumps(obj);
	}

	public static String call(EvaluationContext context, Object obj, String indent)
	{
		return com.livinglogic.ul4on.Utils.dumps(obj, indent);
	}

	public static final Function function = new FunctionAsUL4ON();
}
