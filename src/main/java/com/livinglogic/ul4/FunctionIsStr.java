/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionIsStr extends Function
{
	@Override
	public String getNameUL4()
	{
		return "isstr";
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
		return call(args.get(0));
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof String);
	}

	public static final Function function = new FunctionIsStr();
}
