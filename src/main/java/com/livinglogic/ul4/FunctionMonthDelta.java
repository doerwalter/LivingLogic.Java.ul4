/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionMonthDelta extends Function
{
	@Override
	public String getNameUL4()
	{
		return "monthdelta";
	}

	private static final Signature signature = new Signature().addPositionalOnly("months", 0);

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

	public static MonthDelta call()
	{
		return new MonthDelta();
	}

	public static MonthDelta call(int months)
	{
		return new MonthDelta(months);
	}

	public static MonthDelta call(Object months)
	{
		return call(Utils.toInt(months));
	}
}
