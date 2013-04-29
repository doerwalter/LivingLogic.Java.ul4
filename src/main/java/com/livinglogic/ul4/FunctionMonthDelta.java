/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionMonthDelta extends Function
{
	public String nameUL4()
	{
		return "monthdelta";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"months", 0
		);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
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
