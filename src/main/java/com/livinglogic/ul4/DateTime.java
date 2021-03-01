/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.livinglogic.utils.SetUtils.makeSet;


public class DateTime extends AbstractType
{
	@Override
	public String getNameUL4()
	{
		return "datetime";
	}

	@Override
	public String getDoc()
	{
		return "A date (i.e. year/month/day";
	}

	private static final Signature signature = new Signature("year", Signature.required, "month", Signature.required, "day", Signature.required, "hour", 0, "minute", 0, "second", 0, "microsecond", 0);

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

		return LocalDateTime.of(year, month, day, hour, minute, second, microsecond*1000);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Date || object instanceof LocalDateTime;
	}

	@Override
	public boolean boolInstance(Object instance)
	{
		return true;
	}

	public static SimpleDateFormat formatterDate0 = new SimpleDateFormat("yyyy-MM-dd 00:00");
	public static SimpleDateFormat formatterDate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat formatterDate2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat formatterDate3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'000'");

	private static DateTimeFormatter formatterLocalDateTime0 = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00", Locale.US);
	private static DateTimeFormatter formatterLocalDateTime1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US);
	private static DateTimeFormatter formatterLocalDateTime2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
	private static DateTimeFormatter formatterLocalDateTime3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.US);

	@Override
	public String strInstance(Object instance)
	{
		if (instance instanceof LocalDateTime)
		{
			LocalDateTime dateTime = (LocalDateTime)instance;
			DateTimeFormatter formatter;
			if (dateTime.getNano() != 0)
				formatter = formatterLocalDateTime3;
			else if (dateTime.getSecond() != 0)
				formatter = formatterLocalDateTime2;
			else if (dateTime.getMinute() != 0 || dateTime.getHour() != 0)
				formatter = formatterLocalDateTime1;
			else
				formatter = formatterLocalDateTime0;
			return formatter.format(dateTime);
		}
		else
		{
			Date date = (Date)instance;
			SimpleDateFormat formatter;
			if (BoundDateMethodMicrosecond.call(date) != 0)
				formatter = formatterDate3;
			else if (BoundDateMethodSecond.call(date) != 0)
				formatter = formatterDate2;
			else if (BoundDateMethodMinute.call(date) != 0 || BoundDateMethodHour.call(date) != 0)
				formatter = formatterDate1;
			else
				formatter = formatterDate0;
			return formatter.format(date);
		}
	}

	protected static Set<String> attributes = makeSet("year", "month", "day", "hour", "minute", "second", "microsecond", "date", "weekday", "yearday", "week", "calendar", "isoformat", "mimeformat");

	@Override
	public Set<String> dirInstance(Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		if (object instanceof LocalDateTime)
			return getAttr((LocalDateTime)object, key);
		else
			return getAttr((Date)object, key);
	}

	public Object getAttr(LocalDateTime object, String key)
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
			case "date":
				return new BoundLocalDateTimeMethodDate(object);
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
				return super.getAttr(object, key);
		}
	}

	public Object getAttr(Date object, String key)
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
			case "date":
				return new BoundDateMethodDate(object);
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

	public static UL4Type type = new DateTime();
}
