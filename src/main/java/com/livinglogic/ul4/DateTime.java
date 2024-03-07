/*
** Copyright 2021-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class DateTime extends AbstractType
{
	public static final UL4Type type = new DateTime();

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

		return LocalDateTime.of(year, month, day, hour, minute, second, microsecond*1000);
	}

	public static LocalDateTime call(int year, int month, int day)
	{
		return LocalDateTime.of(year, month, day, 0, 0);
	}

	public static LocalDateTime call(int year, int month, int day, int hour, int minute)
	{
		return LocalDateTime.of(year, month, day, hour, minute);
	}

	public static LocalDateTime call(int year, int month, int day, int hour, int minute, int second)
	{
		return LocalDateTime.of(year, month, day, hour, minute, second);
	}

	public static LocalDateTime call(int year, int month, int day, int hour, int minute, int second, int microsecond)
	{
		return LocalDateTime.of(year, month, day, hour, minute, second, microsecond*1000);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Date || object instanceof LocalDateTime;
	}

	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
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
	public String strInstance(EvaluationContext context, Object instance)
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
			if (DateTime.microsecond(date) != 0)
				formatter = formatterDate3;
			else if (DateTime.second(date) != 0)
				formatter = formatterDate2;
			else if (DateTime.minute(date) != 0 || DateTime.hour(date) != 0)
				formatter = formatterDate1;
			else
				formatter = formatterDate0;
			return formatter.format(date);
		}
	}

	private static final Signature signatureWeekCalendar = new Signature().addBoth("firstweekday", 0).addBoth("mindaysinfirstweek", 4);
	private static final BuiltinMethodDescriptor methodYear = new BuiltinMethodDescriptor(type, "year", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodMonth = new BuiltinMethodDescriptor(type, "month", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodDay = new BuiltinMethodDescriptor(type, "day", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodHour = new BuiltinMethodDescriptor(type, "hour", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodMinute = new BuiltinMethodDescriptor(type, "minute", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodSecond = new BuiltinMethodDescriptor(type, "second", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodMicrosecond = new BuiltinMethodDescriptor(type, "microsecond", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodDate = new BuiltinMethodDescriptor(type, "date", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodWeekday = new BuiltinMethodDescriptor(type, "weekday", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodYearday = new BuiltinMethodDescriptor(type, "yearday", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodWeek = new BuiltinMethodDescriptor(type, "week", signatureWeekCalendar);
	private static final BuiltinMethodDescriptor methodCalendar = new BuiltinMethodDescriptor(type, "calendar", signatureWeekCalendar);
	private static final BuiltinMethodDescriptor methodISOFormat = new BuiltinMethodDescriptor(type, "isoformat", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodMIMEFormat = new BuiltinMethodDescriptor(type, "mimeformat", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodTimestamp = new BuiltinMethodDescriptor(type, "timestamp", Signature.noParameters);

	public static int year(Date instance)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		return calendar.get(Calendar.YEAR);
	}

	public static int year(Date instance, BoundArguments args)
	{
		return year(instance);
	}

	public static int year(LocalDateTime instance)
	{
		return instance.getYear();
	}

	public static int year(LocalDateTime instance, BoundArguments args)
	{
		return year(instance);
	}

	public static int year(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return year((LocalDateTime)instance, args);
		else
			return year((Date)instance, args);
	}

	public static int month(Date instance)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		return calendar.get(Calendar.MONTH)+1;
	}

	public static int month(Date instance, BoundArguments args)
	{
		return month(instance);
	}

	public static int month(LocalDateTime instance)
	{
		return instance.getMonthValue();
	}

	public static int month(LocalDateTime instance, BoundArguments args)
	{
		return month(instance);
	}

	public static int month(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return month((LocalDateTime)instance, args);
		else
			return month((Date)instance, args);
	}

	public static int day(Date instance)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static int day(Date instance, BoundArguments args)
	{
		return day(instance);
	}

	public static int day(LocalDateTime instance)
	{
		return instance.getDayOfMonth();
	}

	public static int day(LocalDateTime instance, BoundArguments args)
	{
		return day(instance);
	}

	public static int day(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return day((LocalDateTime)instance, args);
		else
			return day((Date)instance, args);
	}

	public static int hour(Date instance)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static int hour(Date instance, BoundArguments args)
	{
		return hour(instance);
	}

	public static int hour(LocalDateTime instance)
	{
		return instance.getHour();
	}

	public static int hour(LocalDateTime instance, BoundArguments args)
	{
		return hour(instance);
	}

	public static int hour(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return hour((LocalDateTime)instance, args);
		else
			return hour((Date)instance, args);
	}

	public static int minute(Date instance)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		return calendar.get(Calendar.MINUTE);
	}

	public static int minute(Date instance, BoundArguments args)
	{
		return minute(instance);
	}

	public static int minute(LocalDateTime instance)
	{
		return instance.getMinute();
	}

	public static int minute(LocalDateTime instance, BoundArguments args)
	{
		return minute(instance);
	}

	public static int minute(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return minute((LocalDateTime)instance, args);
		else
			return minute((Date)instance, args);
	}

	public static int second(Date instance)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		return calendar.get(Calendar.SECOND);
	}

	public static int second(Date instance, BoundArguments args)
	{
		return second(instance);
	}

	public static int second(LocalDateTime instance)
	{
		return instance.getSecond();
	}

	public static int second(LocalDateTime instance, BoundArguments args)
	{
		return second(instance);
	}

	public static int second(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return second((LocalDateTime)instance, args);
		else
			return second((Date)instance, args);
	}

	public static int microsecond(Date instance)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		return calendar.get(Calendar.MILLISECOND)*1000;
	}

	public static int microsecond(Date instance, BoundArguments args)
	{
		return microsecond(instance);
	}

	public static int microsecond(LocalDateTime instance)
	{
		return instance.getNano()/1000;
	}

	public static int microsecond(LocalDateTime instance, BoundArguments args)
	{
		return microsecond(instance);
	}

	public static int microsecond(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return microsecond((LocalDateTime)instance, args);
		else
			return microsecond((Date)instance, args);
	}

	public static LocalDate date(Date instance)
	{
		return Utils.toLocalDate(instance);
	}

	public static LocalDate date(Date instance, BoundArguments args)
	{
		return Utils.toLocalDate(instance);
	}

	public static LocalDate date(LocalDateTime instance)
	{
		return instance.toLocalDate();
	}

	public static LocalDate date(LocalDateTime instance, BoundArguments args)
	{
		return date(instance);
	}

	public static LocalDate date(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return date((LocalDateTime)instance, args);
		else
			return date((Date)instance, args);
	}

	public static int weekday(Date instance)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		return javaWeekdays2UL4Weekdays.get(calendar.get(Calendar.DAY_OF_WEEK));
	}

	public static int weekday(Date instance, BoundArguments args)
	{
		return weekday(instance);
	}

	public static int weekday(LocalDateTime instance)
	{
		return instance.getDayOfWeek().getValue()-1;
	}

	public static int weekday(LocalDateTime instance, BoundArguments args)
	{
		return weekday(instance);
	}

	public static int weekday(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return weekday((LocalDateTime)instance, args);
		else
			return weekday((Date)instance, args);
	}

	public static int yearday(Date instance)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	public static int yearday(Date instance, BoundArguments args)
	{
		return yearday(instance);
	}

	public static int yearday(LocalDateTime instance)
	{
		return instance.getDayOfYear();
	}

	public static int yearday(LocalDateTime instance, BoundArguments args)
	{
		return yearday(instance);
	}

	public static int yearday(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return yearday((LocalDateTime)instance, args);
		else
			return yearday((Date)instance, args);
	}

	public static int week(Date instance, int firstWeekday, int minDaysInFirstWeek)
	{
		// Normalize parameters
		firstWeekday %= 7;
		if (minDaysInFirstWeek < 1)
			minDaysInFirstWeek = 1;
		else if (minDaysInFirstWeek > 7)
			minDaysInFirstWeek = 7;

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		calendar.setFirstDayOfWeek(Date_.ul4Weekday2JavaWeekday(firstWeekday));
		calendar.setMinimalDaysInFirstWeek(minDaysInFirstWeek);

		int week = calendar.get(Calendar.WEEK_OF_YEAR);

		return week;
	}

	public static int week(Date instance, BoundArguments args)
	{
		int firstWeekday = args.getInt(0);
		int minDaysInFirstWeek = args.getInt(1);
		return week(instance, firstWeekday, minDaysInFirstWeek);
	}

	public static int week(LocalDateTime instance, int firstWeekday, int minDaysInFirstWeek)
	{
		return calendar(instance, firstWeekday, minDaysInFirstWeek).week;
	}

	public static int week(LocalDateTime instance, BoundArguments args)
	{
		int firstWeekday = args.getInt(0);
		int minDaysInFirstWeek = args.getInt(1);
		return week(instance, firstWeekday, minDaysInFirstWeek);
	}

	public static int week(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return week((LocalDateTime)instance, args);
		else
			return week((Date)instance, args);
	}

	public static Date_.Calendar calendar(Date instance, int firstWeekday, int minDaysInFirstWeek)
	{
		// Normalize parameters
		firstWeekday %= 7;
		if (minDaysInFirstWeek < 1)
			minDaysInFirstWeek = 1;
		else if (minDaysInFirstWeek > 7)
			minDaysInFirstWeek = 7;

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(instance);
		calendar.setFirstDayOfWeek(Date_.ul4Weekday2JavaWeekday(firstWeekday));
		calendar.setMinimalDaysInFirstWeek(minDaysInFirstWeek);

		int year = calendar.getWeekYear();
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);

		return new Date_.Calendar(year, week, Date_.javaWeekday2UL4Weekday(weekday));
	}

	public static List<Integer> calendar(Date instance, BoundArguments args)
	{
		int firstWeekday = args.getInt(0);
		int minDaysInFirstWeek = args.getInt(1);
		return calendar(instance, firstWeekday, minDaysInFirstWeek).asList();
	}

	public static Date_.Calendar calendar(LocalDateTime object, int firstWeekday, int minDaysInFirstWeek)
	{
		return Date_.calendar(object.toLocalDate(), firstWeekday, minDaysInFirstWeek);
	}

	public static List<Integer> calendar(LocalDateTime object, BoundArguments args)
	{
		int firstWeekday = args.getInt(0);
		int minDaysInFirstWeek = args.getInt(1);
		return calendar(object, firstWeekday, minDaysInFirstWeek).asList();
	}

	public static List<Integer> calendar(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return calendar((LocalDateTime)instance, args);
		else
			return calendar((Date)instance, args);
	}

	private static SimpleDateFormat dateISOformatter0 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static SimpleDateFormat dateISOformatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'000'");

	public static String isoformat(Date instance)
	{
		if (microsecond(instance) != 0)
			return dateISOformatter1.format(instance);
		else
			return dateISOformatter0.format(instance);
	}

	public static String evaluate(Date instance, BoundArguments args)
	{
		return isoformat(instance);
	}

	private static DateTimeFormatter localDateTimeISOformatter0 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
	private static DateTimeFormatter localDateTimeISOformatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.US);

	public static String isoformat(LocalDateTime instance)
	{
		DateTimeFormatter formatter;
		if (instance.getNano() != 0)
			formatter = localDateTimeISOformatter1;
		else
			formatter = localDateTimeISOformatter0;
		return instance.format(formatter);
	}

	public static String isoformat(LocalDateTime instance, BoundArguments args)
	{
		return isoformat(instance);
	}

	public static String isoformat(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return isoformat((LocalDateTime)instance, args);
		else
			return isoformat((Date)instance, args);
	}

	private static SimpleDateFormat mimeDateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

	public static String mimeformat(Date instance)
	{
		return mimeDateFormatter.format(instance);
	}

	public static String mimeformat(Date instance, BoundArguments args)
	{
		return mimeformat(instance);
	}

	private static DateTimeFormatter localDateTimeMIMEFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

	public static String mimeformat(LocalDateTime instance)
	{
		return instance.format(localDateTimeMIMEFormatter);
	}

	public static String mimeformat(LocalDateTime instance, BoundArguments args)
	{
		return mimeformat(instance);
	}

	public static String mimeformat(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return mimeformat((LocalDateTime)instance, args);
		else
			return mimeformat((Date)instance, args);
	}

	public static Double timestamp(Date instance) {
		return timestamp(instance.toInstant());
	}

	public static Double timestamp(Date instance, BoundArguments args) {
		return timestamp(instance);
	}

	public static Double timestamp(LocalDateTime instance) {
		return timestamp(instance.toInstant(ZoneOffset.systemDefault().getRules().getOffset(instance)));
	}

	public static Double timestamp(LocalDateTime instance, BoundArguments args) {
		return timestamp(instance);
	}

	public static Double timestamp(Instant instance)
	{
		var microSeconds = TimeUnit.NANOSECONDS.toMicros(instance.getNano());

		return Double.valueOf(instance.getEpochSecond()) + microSeconds/1_000_000.0;
	}

	public static Double timestamp(Object instance, BoundArguments args)
	{
		if (instance instanceof LocalDateTime)
			return timestamp((LocalDateTime)instance, args);
		else
			return timestamp((Date)instance, args);
	}

	protected static Set<String> attributes = Set.of("year", "month", "day", "hour", "minute", "second", "microsecond", "date", "weekday", "yearday", "week", "calendar", "isoformat", "mimeformat", "timestamp");

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(EvaluationContext context, Object instance, String key)
	{
		switch (key)
		{
			case "year":
				return methodYear.bindMethod(instance);
			case "month":
				return methodMonth.bindMethod(instance);
			case "day":
				return methodDay.bindMethod(instance);
			case "hour":
				return methodHour.bindMethod(instance);
			case "minute":
				return methodMinute.bindMethod(instance);
			case "second":
				return methodSecond.bindMethod(instance);
			case "microsecond":
				return methodMicrosecond.bindMethod(instance);
			case "date":
				return methodDate.bindMethod(instance);
			case "weekday":
				return methodWeekday.bindMethod(instance);
			case "yearday":
				return methodYearday.bindMethod(instance);
			case "week":
				return methodWeek.bindMethod(instance);
			case "calendar":
				return methodCalendar.bindMethod(instance);
			case "isoformat":
				return methodISOFormat.bindMethod(instance);
			case "mimeformat":
				return methodMIMEFormat.bindMethod(instance);
			case "timestamp":
				return methodTimestamp.bindMethod(instance);
			default:
				return super.getAttr(context, instance, key);
		}
	}

	public Object callAttr(EvaluationContext context, Object instance, String key, List<Object> args, Map<String, Object> kwargs)
	{
		switch (key)
		{
			case "year":
				return year(instance, methodYear.bindArguments(args, kwargs));
			case "month":
				return month(instance, methodMonth.bindArguments(args, kwargs));
			case "day":
				return day(instance, methodDay.bindArguments(args, kwargs));
			case "hour":
				return hour(instance, methodHour.bindArguments(args, kwargs));
			case "minute":
				return minute(instance, methodMinute.bindArguments(args, kwargs));
			case "second":
				return second(instance, methodSecond.bindArguments(args, kwargs));
			case "microsecond":
				return microsecond(instance, methodMicrosecond.bindArguments(args, kwargs));
			case "date":
				return date(instance, methodDate.bindArguments(args, kwargs));
			case "weekday":
				return weekday(instance, methodWeekday.bindArguments(args, kwargs));
			case "yearday":
				return yearday(instance, methodYearday.bindArguments(args, kwargs));
			case "week":
				return week(instance, methodWeek.bindArguments(args, kwargs));
			case "calendar":
				return calendar(instance, methodCalendar.bindArguments(args, kwargs));
			case "isoformat":
				return isoformat(instance, methodISOFormat.bindArguments(args, kwargs));
			case "mimeformat":
				return mimeformat(instance, methodMIMEFormat.bindArguments(args, kwargs));
			case "timestamp":
				return timestamp(instance, methodTimestamp.bindArguments(args, kwargs));
			default:
				return super.callAttr(context, instance, key, args, kwargs);
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
