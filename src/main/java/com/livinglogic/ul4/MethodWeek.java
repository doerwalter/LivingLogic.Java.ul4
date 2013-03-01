/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class MethodWeek extends NormalMethod
{
	public String nameUL4()
	{
		return "week";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("firstweekday", null);
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj, args[0]);
	}

	public static int call(Date obj)
	{
		return call(obj, 0);
	}

	public static int call(Object obj)
	{
		return call(obj, 0);
	}

	public static int call(Date obj, int firstWeekday)
	{
		int yearday = MethodYearday.call(obj)+6;
		int jan1Weekday = MethodWeekday.call(FunctionDate.call(MethodYear.call(obj), 1, 1));
		while (jan1Weekday != firstWeekday)
		{
			--yearday;
			jan1Weekday = (++jan1Weekday) % 7;
		}
		return yearday/7;
	}

	public static int call(Object obj, int firstWeekday)
	{
		if (obj instanceof Date)
			return call((Date)obj, firstWeekday);
		throw new ArgumentTypeMismatchException("{}.week()", obj);
	}

	public static int call(Object obj, Object firstWeekday)
	{
		return call(obj, firstWeekday == null ? 0 : Utils.toInt(firstWeekday));
	}
}
