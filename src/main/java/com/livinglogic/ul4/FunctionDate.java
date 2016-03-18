/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FunctionDate extends Function
{
	public String nameUL4()
	{
		return "date";
	}

	private static final Signature signature = new Signature("year", Signature.required, "month", Signature.required, "day", Signature.required, "hour", 0, "minute", 0, "second", 0, "microsecond", 0);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5), args.get(6));
	}

	public static Date call(int year, int month, int day)
	{
		return call(year, month, day, 0, 0, 0, 0);
	}

	public static Date call(int year, int month, int day, int hour)
	{
		return call(year, month, day, hour, 0, 0, 0);
	}

	public static Date call(int year, int month, int day, int hour, int minute)
	{
		return call(year, month, day, hour, minute, 0, 0);
	}

	public static Date call(int year, int month, int day, int hour, int minute, int second)
	{
		return call(year, month, day, hour, minute, second, 0);
	}

	public static Date call(int year, int month, int day, int hour, int minute, int second, int microsecond)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month-1, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, microsecond/1000);
		return calendar.getTime();
	}

	public static Date call(Object year, Object month, Object day)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day));
	}

	public static Date call(Object year, Object month, Object day, Object hour)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day), Utils.toInt(hour));
	}

	public static Date call(Object year, Object month, Object day, Object hour, Object minute)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day), Utils.toInt(hour), Utils.toInt(minute));
	}

	public static Date call(Object year, Object month, Object day, Object hour, Object minute, Object second)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day), Utils.toInt(hour), Utils.toInt(minute), Utils.toInt(second));
	}

	public static Date call(Object year, Object month, Object day, Object hour, Object minute, Object second, Object microsecond)
	{
		return call(Utils.toInt(year), Utils.toInt(month), Utils.toInt(day), Utils.toInt(hour), Utils.toInt(minute), Utils.toInt(second), Utils.toInt(microsecond));
	}
}
