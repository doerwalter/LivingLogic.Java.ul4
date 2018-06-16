/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
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

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return weekDays.get(calendar.get(Calendar.DAY_OF_WEEK));
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}

	private static HashMap<Integer, Integer> weekDays;

	static
	{
		weekDays = new HashMap<Integer, Integer>();
		weekDays.put(Calendar.MONDAY, 0);
		weekDays.put(Calendar.TUESDAY, 1);
		weekDays.put(Calendar.WEDNESDAY, 2);
		weekDays.put(Calendar.THURSDAY, 3);
		weekDays.put(Calendar.FRIDAY, 4);
		weekDays.put(Calendar.SATURDAY, 5);
		weekDays.put(Calendar.SUNDAY, 6);
	}
}
