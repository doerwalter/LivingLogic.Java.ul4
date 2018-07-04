/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;

public class BoundLocalDateMethodWeek extends BoundMethod<LocalDate>
{
	public BoundLocalDateMethodWeek(LocalDate object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.week";
	}

	private static final Signature signature = new Signature("firstweekday", 0, "mindaysinfirstweek", 4);

	public Signature getSignature()
	{
		return signature;
	}

	public static int call(LocalDate object, int firstWeekday, int minDaysInFirstWeek)
	{
		return BoundLocalDateMethodCalendar.call(object, firstWeekday, minDaysInFirstWeek).week;
	}

	public Object evaluate(BoundArguments args)
	{
		int firstWeekday = Utils.toInt(args.get(0));
		int minDaysInFirstWeek = Utils.toInt(args.get(1));
		return call(object, firstWeekday, minDaysInFirstWeek);
	}
}
