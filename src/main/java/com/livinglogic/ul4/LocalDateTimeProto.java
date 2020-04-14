/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashMap;
import java.util.Calendar;

import static com.livinglogic.utils.SetUtils.makeSet;


public class LocalDateTimeProto extends Proto
{
	public static Proto proto = new LocalDateTimeProto();

	public static String name = "datetime";

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public boolean bool(Object object)
	{
		return bool((LocalDateTime)object);
	}

	public static boolean bool(LocalDateTime object)
	{
		return object != null;
	}

	protected static Set<String> attrNames = makeSet("year", "month", "day", "hour", "minute", "second", "microsecond", "weekday", "yearday", "week", "calendar", "isoformat", "mimeformat");

	@Override
	public Set<String> getAttrNames(Object object)
	{
		return attrNames;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		return getAttr((LocalDateTime)object, key);
	}

	public static Object getAttr(LocalDateTime object, String key)
	{
		switch (key)
		{
			case "year":
				return new BoundLocalDateTimeMethodYear(object);
			case "month":
				return new BoundLocalDateTimeMethodMonth(object);
			case "day":
				return new BoundLocalDateTimeMethodDay(object);
			case "hour":
				return new BoundLocalDateTimeMethodHour(object);
			case "minute":
				return new BoundLocalDateTimeMethodMinute(object);
			case "second":
				return new BoundLocalDateTimeMethodSecond(object);
			case "microsecond":
				return new BoundLocalDateTimeMethodMicrosecond(object);
			case "weekday":
				return new BoundLocalDateTimeMethodWeekday(object);
			case "yearday":
				return new BoundLocalDateTimeMethodYearday(object);
			case "week":
				return new BoundLocalDateTimeMethodWeek(object);
			case "calendar":
				return new BoundLocalDateTimeMethodCalendar(object);
			case "isoformat":
				return new BoundLocalDateTimeMethodISOFormat(object);
			case "mimeformat":
				return new BoundLocalDateTimeMethodMIMEFormat(object);
			default:
				throw new AttributeException(object, key);
		}
	}

	static int javaWeekday2UL4Weekday(int javaWeekday)
	{
		return javaWeekdays2UL4Weekdays.get(javaWeekday);
	}

	static int ul4Weekday2JavaWeekday(int ul4Weekday)
	{
		return ul4Weekdays2JavaWeekdays.get(ul4Weekday);
	}

	private static HashMap<Integer, Integer> javaWeekdays2UL4Weekdays;

	static
	{
		javaWeekdays2UL4Weekdays = new HashMap<Integer, Integer>();
		javaWeekdays2UL4Weekdays.put(Calendar.MONDAY, 0);
		javaWeekdays2UL4Weekdays.put(Calendar.TUESDAY, 1);
		javaWeekdays2UL4Weekdays.put(Calendar.WEDNESDAY, 2);
		javaWeekdays2UL4Weekdays.put(Calendar.THURSDAY, 3);
		javaWeekdays2UL4Weekdays.put(Calendar.FRIDAY, 4);
		javaWeekdays2UL4Weekdays.put(Calendar.SATURDAY, 5);
		javaWeekdays2UL4Weekdays.put(Calendar.SUNDAY, 6);
	}

	private static HashMap<Integer, Integer> ul4Weekdays2JavaWeekdays;

	static
	{
		ul4Weekdays2JavaWeekdays = new HashMap<Integer, Integer>();
		ul4Weekdays2JavaWeekdays.put(0, Calendar.MONDAY);
		ul4Weekdays2JavaWeekdays.put(1, Calendar.TUESDAY);
		ul4Weekdays2JavaWeekdays.put(2, Calendar.WEDNESDAY);
		ul4Weekdays2JavaWeekdays.put(3, Calendar.THURSDAY);
		ul4Weekdays2JavaWeekdays.put(4, Calendar.FRIDAY);
		ul4Weekdays2JavaWeekdays.put(5, Calendar.SATURDAY);
		ul4Weekdays2JavaWeekdays.put(6, Calendar.SUNDAY);
	}
}
