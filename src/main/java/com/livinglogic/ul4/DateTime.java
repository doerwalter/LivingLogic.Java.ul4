/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class DateTime extends AbstractType
{
	protected DateTime()
	{
		super(null, "datetime", null, "A date (i.e. year/month/day");
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
	public boolean toBool(Object object)
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
	public String toStr(Object object)
	{
		if (object instanceof LocalDateTime)
		{
			LocalDateTime dateTime = (LocalDateTime)object;
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
			Date date = (Date)object;
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

	public static UL4Type type = new DateTime();
}
