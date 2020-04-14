/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashMap;

import java.util.Arrays;

import static com.livinglogic.utils.SetUtils.makeSet;


public class DateProto extends Proto
{
	public static Proto proto = new DateProto();

	public static String name = "datetime";

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public boolean bool(Object object)
	{
		return bool((Date)object);
	}

	public static boolean bool(Date object)
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
		return getAttr((Date)object, key);
	}

	public static Object getAttr(Date object, String key)
	{
		switch (key)
		{
			case "year":
				return new BoundDateMethodYear(object);
			case "month":
				return new BoundDateMethodMonth(object);
			case "day":
				return new BoundDateMethodDay(object);
			case "hour":
				return new BoundDateMethodHour(object);
			case "minute":
				return new BoundDateMethodMinute(object);
			case "second":
				return new BoundDateMethodSecond(object);
			case "microsecond":
				return new BoundDateMethodMicrosecond(object);
			case "weekday":
				return new BoundDateMethodWeekday(object);
			case "yearday":
				return new BoundDateMethodYearday(object);
			case "week":
				return new BoundDateMethodWeek(object);
			case "calendar":
				return new BoundDateMethodCalendar(object);
			case "isoformat":
				return new BoundDateMethodISOFormat(object);
			case "mimeformat":
				return new BoundDateMethodMIMEFormat(object);
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
		javaWeekdays2UL4Weekdays.put(java.util.Calendar.MONDAY, 0);
		javaWeekdays2UL4Weekdays.put(java.util.Calendar.TUESDAY, 1);
		javaWeekdays2UL4Weekdays.put(java.util.Calendar.WEDNESDAY, 2);
		javaWeekdays2UL4Weekdays.put(java.util.Calendar.THURSDAY, 3);
		javaWeekdays2UL4Weekdays.put(java.util.Calendar.FRIDAY, 4);
		javaWeekdays2UL4Weekdays.put(java.util.Calendar.SATURDAY, 5);
		javaWeekdays2UL4Weekdays.put(java.util.Calendar.SUNDAY, 6);
	}

	private static HashMap<Integer, Integer> ul4Weekdays2JavaWeekdays;

	static
	{
		ul4Weekdays2JavaWeekdays = new HashMap<Integer, Integer>();
		ul4Weekdays2JavaWeekdays.put(0, java.util.Calendar.MONDAY);
		ul4Weekdays2JavaWeekdays.put(1, java.util.Calendar.TUESDAY);
		ul4Weekdays2JavaWeekdays.put(2, java.util.Calendar.WEDNESDAY);
		ul4Weekdays2JavaWeekdays.put(3, java.util.Calendar.THURSDAY);
		ul4Weekdays2JavaWeekdays.put(4, java.util.Calendar.FRIDAY);
		ul4Weekdays2JavaWeekdays.put(5, java.util.Calendar.SATURDAY);
		ul4Weekdays2JavaWeekdays.put(6, java.util.Calendar.SUNDAY);
	}

	public static class Calendar
	{
		public int year;
		public int week;
		public int weekday;

		public Calendar(int year, int week, int weekday)
		{
			this.year = year;
			this.week = week;
			this.weekday = weekday;
		}

		public List<Integer> asList()
		{
			return Arrays.asList(year, week, weekday);
		}
	}
}
