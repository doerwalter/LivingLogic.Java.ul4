/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class MethodWeekday implements Method
{
	public String getName()
	{
		return "weekday";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(obj);
			default:
				throw new ArgumentCountMismatchException("method", "weekday", args.length, 0);
		}
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return weekdays.get(calendar.get(Calendar.DAY_OF_WEEK));
	}

	public static int call(Object obj)
	{
		if (obj instanceof Date)
			return call((Date)obj);
		throw new ArgumentTypeMismatchException("{}.weekday()", obj);
	}

	private static HashMap<Integer, Integer> weekdays;

	static
	{
		weekdays = new HashMap<Integer, Integer>();
		weekdays.put(Calendar.MONDAY, 0);
		weekdays.put(Calendar.TUESDAY, 1);
		weekdays.put(Calendar.WEDNESDAY, 2);
		weekdays.put(Calendar.THURSDAY, 3);
		weekdays.put(Calendar.FRIDAY, 4);
		weekdays.put(Calendar.SATURDAY, 5);
		weekdays.put(Calendar.SUNDAY, 6);
	}
}
