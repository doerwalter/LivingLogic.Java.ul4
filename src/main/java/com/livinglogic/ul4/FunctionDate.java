/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;

public class FunctionDate extends Function
{
	public String nameUL4()
	{
		return "date";
	}

	private static final Signature signature = new Signature("year", Signature.required, "month", Signature.required, "day", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0), args.get(1), args.get(2));
	}

	public static LocalDate call(int year, int month, int day)
	{
		return LocalDate.of(year, month, day);
	}

	public static LocalDate call(Object year, Object month, Object day)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day));
	}
}
