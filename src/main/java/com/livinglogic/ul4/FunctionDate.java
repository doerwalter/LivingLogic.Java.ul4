/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FunctionDate extends NormalFunction
{
	public String getName()
	{
		return "date";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("year");
		signature.add("month");
		signature.add("day");
		signature.add("hour", 0);
		signature.add("minute", 0);
		signature.add("second", 0);
		signature.add("microsecond", 0);
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
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
