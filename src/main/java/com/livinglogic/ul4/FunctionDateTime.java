/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;

public class FunctionDateTime extends Function
{
	@Override
	public String getNameUL4()
	{
		return "datetime";
	}

	private static final Signature signature = new Signature().addBoth("year").addBoth("month").addBoth("day").addBoth("hour", 0).addBoth("minute", 0).addBoth("second", 0).addBoth("microsecond", 0);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5), args.get(6));
	}

	public static LocalDateTime call(int year, int month, int day)
	{
		return LocalDateTime.of(year, month, day, 0, 0);
	}

	public static LocalDateTime call(int year, int month, int day, int hour, int minute)
	{
		return LocalDateTime.of(year, month, day, hour, minute);
	}

	public static LocalDateTime call(int year, int month, int day, int hour, int minute, int second)
	{
		return LocalDateTime.of(year, month, day, hour, minute, second);
	}

	public static LocalDateTime call(int year, int month, int day, int hour, int minute, int second, int microsecond)
	{
		return LocalDateTime.of(year, month, day, hour, minute, second, microsecond*1000);
	}

	public static LocalDateTime call(Object year, Object month, Object day)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day));
	}

	public static LocalDateTime call(Object year, Object month, Object day, Object hour, Object minute)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day), Utils.toInt(hour), Utils.toInt(minute));
	}

	public static LocalDateTime call(Object year, Object month, Object day, Object hour, Object minute, Object second)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day), Utils.toInt(hour), Utils.toInt(minute), Utils.toInt(second));
	}

	public static LocalDateTime call(Object year, Object month, Object day, Object hour, Object minute, Object second, Object microsecond)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day), Utils.toInt(hour), Utils.toInt(minute), Utils.toInt(second), Utils.toInt(microsecond));
	}
}
