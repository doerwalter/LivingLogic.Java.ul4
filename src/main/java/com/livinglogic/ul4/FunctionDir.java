/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionDir extends Function
{
	@Override
	public String getNameUL4()
	{
		return "dir";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj");

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
		return UL4Type.getType(obj).dirInstance(context, obj);
	}

	public static FunctionDir function = new FunctionDir();
}
