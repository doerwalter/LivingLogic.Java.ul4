/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class BoundDateMethodWeekday extends BoundMethod<Date>
{
	public BoundDateMethodWeekday(Date object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.weekday";
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

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return weekdays.get(calendar.get(Calendar.DAY_OF_WEEK));
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
