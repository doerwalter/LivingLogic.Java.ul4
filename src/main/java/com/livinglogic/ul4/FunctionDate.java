/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FunctionDate extends Function
{
	@Override
	public String getNameUL4()
	{
		return "date";
	}

	private static final Signature signature = new Signature("year", Signature.required, "month", Signature.required, "day", Signature.required, "hour", 0, "minute", 0, "second", 0, "microsecond", 0);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		if (((Integer)args.get(3) == 0) && ((Integer)args.get(4) == 0) && ((Integer)args.get(5) == 0) && ((Integer)args.get(6) == 0))
			return call(args.get(0), args.get(1), args.get(2));
		else
			return call(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5), args.get(6));
	}

	public static LocalDate call(int year, int month, int day)
	{
		return LocalDate.of(year, month, day);
	}

	public static LocalDate call(Object year, Object month, Object day)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day));
	}

	public static LocalDateTime call(int year, int month, int day, int hour, int minute, int second, int microsecond)
	{
		return LocalDateTime.of(year, month, day, hour, minute, second, microsecond*1000);
	}

	public static LocalDateTime call(Object year, Object month, Object day, Object hour, Object minute, Object second, Object microsecond)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day), Utils.toInt(hour), Utils.toInt(minute), Utils.toInt(second), Utils.toInt(microsecond));
	}
}
