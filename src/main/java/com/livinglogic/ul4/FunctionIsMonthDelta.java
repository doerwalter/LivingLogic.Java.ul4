/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionIsMonthDelta extends Function
{
	@Override
	public String getNameUL4()
	{
		return "ismonthdelta";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj");

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

	public static boolean call(Object obj)
	{
		return MonthDelta.type.instanceCheck(obj);
	}

	public static final Function function = new FunctionIsMonthDelta();
}

