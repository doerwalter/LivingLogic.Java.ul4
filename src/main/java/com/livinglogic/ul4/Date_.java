/*
** Copyright 2021-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Locale;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;


public class Date_ extends AbstractType
{
	public static final Date_ type = new Date_();

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
	public Object create(EvaluationContext context, BoundArguments arguments)
	{
		int year = arguments.getInt(0);
		int month = arguments.getInt(1);
		int day = arguments.getInt(2);
		int hour = arguments.getInt(3);
		int minute = arguments.getInt(4);
		int second = arguments.getInt(5);
		int microsecond = arguments.getInt(6);

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
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		return true;
	}

	private static DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);

	@Override
	public String strInstance(EvaluationContext context, Object instance)
	{
		return formatterLocalDate.format((LocalDate)instance);
	}

	private static final Signature signatureWeekCalendar = new Signature().addBoth("firstweekday", 0).addBoth("mindaysinfirstweek", 4);
	private static final BuiltinMethodDescriptor methodYear = new BuiltinMethodDescriptor(type, "year", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodMonth = new BuiltinMethodDescriptor(type, "month", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodDay = new BuiltinMethodDescriptor(type, "day", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodDate = new BuiltinMethodDescriptor(type, "date", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodWeekday = new BuiltinMethodDescriptor(type, "weekday", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodYearday = new BuiltinMethodDescriptor(type, "yearday", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodWeek = new BuiltinMethodDescriptor(type, "week", signatureWeekCalendar);
	private static final BuiltinMethodDescriptor methodCalendar = new BuiltinMethodDescriptor(type, "calendar", signatureWeekCalendar);
	private static final BuiltinMethodDescriptor methodISOFormat = new BuiltinMethodDescriptor(type, "isoformat", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodMIMEFormat = new BuiltinMethodDescriptor(type, "mimeformat", Signature.noParameters);

	public static int year(LocalDate instance)
	{
		return instance.getYear();
	}

	public static int year(LocalDate instance, BoundArguments args)
	{
		return year(instance);
	}

	public static int month(LocalDate instance)
	{
		return instance.getMonthValue();
	}

	public static int month(LocalDate instance, BoundArguments args)
	{
		return month(instance);
	}

	public static int day(LocalDate instance)
	{
		return instance.getDayOfMonth();
	}

	public static int day(LocalDate instance, BoundArguments args)
	{
		return day(instance);
	}

	public static LocalDate date(LocalDate instance)
	{
		return instance;
	}

	public static LocalDate date(LocalDate instance, BoundArguments args)
	{
		return date(instance);
	}

	public static int weekday(LocalDate instance)
	{
		return instance.getDayOfWeek().getValue()-1;
	}

	public static int weekday(LocalDate instance, BoundArguments args)
	{
		return weekday(instance);
	}

	public static int yearday(LocalDate instance)
	{
		return instance.getDayOfYear();
	}

	public static int yearday(LocalDate instance, BoundArguments args)
	{
		return yearday(instance);
	}

	public static int week(LocalDate instance, int firstWeekday, int minDaysInFirstWeek)
	{
		return calendar(instance, firstWeekday, minDaysInFirstWeek).week;
	}

	public static int week(LocalDate instance, BoundArguments args)
	{
		int firstWeekday = args.getInt(0);
		int minDaysInFirstWeek = args.getInt(1);
		return week(instance, firstWeekday, minDaysInFirstWeek);
	}

	public static Calendar calendar(LocalDate instance, int firstWeekday, int minDaysInFirstWeek)
	{
		// Normalize parameters
		firstWeekday %= 7;
		if (minDaysInFirstWeek < 1)
			minDaysInFirstWeek = 1;
		else if (minDaysInFirstWeek > 7)
			minDaysInFirstWeek = 7;

		// {@code instance} might be in the first week of the next year, or last week of
		// the previous year, so we might have to try those too.
		int year = instance.getYear();
		for (int refYear = year+1; refYear >= year-1; --refYear)
		{
			// {@code refDate} will always be in week 1
			LocalDate refDate = LocalDate.of(refYear, 1, minDaysInFirstWeek);
			// Go back to the start of {@code refDate}s week (i.e. day 1 of week 1)
			LocalDate weekStartDate = refDate.minusDays(ModAST.call(refDate.getDayOfWeek().getValue() - 1 - firstWeekday, 7));
			// Is our date {@code instance} at or after day 1 of week 1?
			// (if not we have to calculate its week number based on the year before)
			if (!instance.isBefore(weekStartDate))
			{
				long refDays = ChronoUnit.DAYS.between(weekStartDate, instance);
				int refWeekDay = weekday(instance);
				return new Calendar(refYear, (int)(refDays/7+1), refWeekDay);
			}
		}

		// Can't happen
		return null;
	}

	public List<Integer> calendar(LocalDate instance, BoundArguments args)
	{
		int firstWeekday = args.getInt(0);
		int minDaysInFirstWeek = args.getInt(1);
		return calendar(instance, firstWeekday, minDaysInFirstWeek).asList();
	}

	private static DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);

	public static String isoformat(LocalDate instance)
	{
		return instance.format(isoFormatter);
	}

	public static String isoformat(LocalDate instance, BoundArguments args)
	{
		return isoformat(instance);
	}

	private static DateTimeFormatter mimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.US);

	public static String mimeformat(LocalDate instance)
	{
		return instance.format(mimeFormatter);
	}

	public static String mimeformat(LocalDate instance, BoundArguments args)
	{
		return mimeformat(instance);
	}

	protected static Set<String> attributes = Set.of("year", "month", "day", "date", "weekday", "yearday", "week", "calendar", "isoformat", "mimeformat");

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(EvaluationContext context, Object instance, String key)
	{
		LocalDate date = (LocalDate)instance;

		switch (key)
		{
			case "year":
				return methodYear.bindMethod(date);
			case "month":
				return methodMonth.bindMethod(date);
			case "day":
				return methodDay.bindMethod(date);
			case "date":
				return methodDate.bindMethod(date);
			case "weekday":
				return methodWeekday.bindMethod(date);
			case "yearday":
				return methodYearday.bindMethod(date);
			case "week":
				return methodWeek.bindMethod(date);
			case "calendar":
				return methodCalendar.bindMethod(date);
			case "isoformat":
				return methodISOFormat.bindMethod(date);
			case "mimeformat":
				return methodMIMEFormat.bindMethod(date);
			default:
				return super.getAttr(context, instance, key);
		}
	}

	@Override
	public Object callAttr(EvaluationContext context, Object instance, String key, List<Object> args, Map<String, Object> kwargs)
	{
		LocalDate date = (LocalDate)instance;

		switch (key)
		{
			case "year":
				return year(date, methodYear.bindArguments(args, kwargs));
			case "month":
				return month(date, methodMonth.bindArguments(args, kwargs));
			case "day":
				return day(date, methodDay.bindArguments(args, kwargs));
			case "date":
				return date(date, methodDate.bindArguments(args, kwargs));
			case "weekday":
				return weekday(date, methodWeekday.bindArguments(args, kwargs));
			case "yearday":
				return yearday(date, methodYearday.bindArguments(args, kwargs));
			case "week":
				return week(date, methodWeek.bindArguments(args, kwargs));
			case "calendar":
				return calendar(date, methodCalendar.bindArguments(args, kwargs));
			case "isoformat":
				return isoformat(date, methodISOFormat.bindArguments(args, kwargs));
			case "mimeformat":
				return mimeformat(date, methodMIMEFormat.bindArguments(args, kwargs));
			default:
				return super.callAttr(context, date, key, args, kwargs);
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
			return List.of(year, week, weekday);
		}
	}
}
