/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BoundDateMethodWeek extends BoundMethod<Date>
{
	public BoundDateMethodWeek(Date object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "week";
	}

	private static final Signature signature = new Signature().addBoth("firstweekday", 0).addBoth("mindaysinfirstweek", 4);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static int call(Date object, int firstWeekday, int minDaysInFirstWeek)
	{
		// Normalize parameters
		firstWeekday %= 7;
		if (minDaysInFirstWeek < 1)
			minDaysInFirstWeek = 1;
		else if (minDaysInFirstWeek > 7)
			minDaysInFirstWeek = 7;

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(object);
		calendar.setFirstDayOfWeek(Date_.ul4Weekday2JavaWeekday(firstWeekday));
		calendar.setMinimalDaysInFirstWeek(minDaysInFirstWeek);

		int week = calendar.get(Calendar.WEEK_OF_YEAR);

		return week;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		int firstWeekday = Utils.toInt(args.get(0));
		int minDaysInFirstWeek = Utils.toInt(args.get(1));
		return call(object, firstWeekday, minDaysInFirstWeek);
	}
}
