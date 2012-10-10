/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class MethodWeek implements Method
{
	public String getName()
	{
		return "week";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(obj);
			case 1:
				return call(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "week", args.length, 0, 1);
		}
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
