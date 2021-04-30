/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Locale;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.livinglogic.utils.SetUtils.makeSet;


public class Date_ extends AbstractType
{
	@Override
	public String getNameUL4()
	{
		return "date";
	}

	@Override
	public String getDoc()
	{
		return "A date (i.e. year/month/day)";
	}

	private static final Signature signature = new Signature().addBoth("year").addBoth("month").addBoth("day").addBoth("hour", 0).addBoth("minute", 0).addBoth("second", 0).addBoth("microsecond", 0);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(BoundArguments arguments)
	{
		int year = Utils.toInt(arguments.get(0));
		int month = Utils.toInt(arguments.get(1));
		int day = Utils.toInt(arguments.get(2));
		int hour = Utils.toInt(arguments.get(3));
		int minute = Utils.toInt(arguments.get(4));
		int second = Utils.toInt(arguments.get(5));
		int microsecond = Utils.toInt(arguments.get(6));

		if (hour == 0 && minute == 0 && second == 0 && microsecond == 0)
			return LocalDate.of(year, month, day);
		else
			return LocalDateTime.of(year, month, day, hour, minute, second, microsecond*1000);
	}

	public static LocalDate call(int year, int month, int day)
	{
		return LocalDate.of(year, month, day);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof LocalDate;
	}

	@Override
	public boolean boolInstance(Object instance)
	{
		return true;
	}

	private static DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);

	@Override
	public String strInstance(Object instance)
	{
		return formatterLocalDate.format((LocalDate)instance);
	}

	protected static Set<String> attributes = makeSet("year", "month", "day", "date", "weekday", "yearday", "week", "calendar", "isoformat", "mimeformat");

	@Override
	public Set<String> dirInstance(Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		LocalDate date = (LocalDate)object;

		switch (key)
		{
			case "year":
				return new BoundLocalDateMethodYear(date);
			case "month":
				return new BoundLocalDateMethodMonth(date);
			case "day":
				return new BoundLocalDateMethodDay(date);
			case "date":
				return new BoundLocalDateMethodDate(date);
			case "weekday":
				return new BoundLocalDateMethodWeekday(date);
			case "yearday":
				return new BoundLocalDateMethodYearday(date);
			case "week":
				return new BoundLocalDateMethodWeek(date);
			case "calendar":
				return new BoundLocalDateMethodCalendar(date);
			case "isoformat":
				return new BoundLocalDateMethodISOFormat(date);
			case "mimeformat":
				return new BoundLocalDateMethodMIMEFormat(date);
			default:
				return super.getAttr(object, key);
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

	public static final Date_ type = new Date_();
}
