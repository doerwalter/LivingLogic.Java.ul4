/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class Date_ extends AbstractType
{
	protected Date_()
	{
		super(null, "date", null, "A date (i.e. year/month/day");
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

		if (hour == 0 && minute == 0 && second == 0 && microsecond == 0)
			return LocalDate.of(year, month, day);
		else
			return LocalDateTime.of(year, month, day, hour, minute, second, microsecond*1000);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof LocalDate;
	}

	@Override
	public boolean toBool(Object object)
	{
		return true;
	}

	private static DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);

	@Override
	public String toStr(Object object)
	{
		return formatterLocalDate.format((LocalDate)object);
	}

	public static UL4Type type = new Date_();
}
