/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.GregorianCalendar;
import static java.util.Arrays.asList;

public class BoundDateMethodCalendar extends BoundMethod<Date>
{
	public BoundDateMethodCalendar(Date object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "calendar";
	}

	private static final Signature signature = new Signature("firstweekday", 0, "mindaysinfirstweek", 4);

	public Signature getSignature()
	{
		return signature;
	}

	public static DateProto.Calendar call(Date object, int firstWeekday, int minDaysInFirstWeek)
	{
		// Normalize parameters
		firstWeekday %= 7;
		if (minDaysInFirstWeek < 1)
			minDaysInFirstWeek = 1;
		else if (minDaysInFirstWeek > 7)
			minDaysInFirstWeek = 7;

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(object);
		calendar.setFirstDayOfWeek(DateProto.ul4Weekday2JavaWeekday(firstWeekday));
		calendar.setMinimalDaysInFirstWeek(minDaysInFirstWeek);

		int year = calendar.getWeekYear();
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);

		return new DateProto.Calendar(year, week, DateProto.javaWeekday2UL4Weekday(weekday));
	}

	public Object evaluate(BoundArguments args)
	{
		int firstWeekday = Utils.toInt(args.get(0));
		int minDaysInFirstWeek = Utils.toInt(args.get(1));
		return call(object, firstWeekday, minDaysInFirstWeek).asList();
	}
}
