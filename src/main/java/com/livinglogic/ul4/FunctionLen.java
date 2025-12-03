/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class FunctionLen extends Function
{
	@Override
	public String getNameUL4()
	{
		return "len";
	}

	private static final Signature signature = new Signature().addPositionalOnly("sequence");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0));
	}

	public static Object call(EvaluationContext context, Object obj)
	{
		return UL4Type.getType(obj).lenInstance(context, obj);
	}

	public static final Function function = new FunctionLen();
}
