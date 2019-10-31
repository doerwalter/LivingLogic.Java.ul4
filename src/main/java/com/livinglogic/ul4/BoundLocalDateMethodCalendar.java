/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BoundLocalDateMethodCalendar extends BoundMethod<LocalDate>
{
	public BoundLocalDateMethodCalendar(LocalDate object)
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

	public static DateProto.Calendar call(LocalDate object, int firstWeekday, int minDaysInFirstWeek)
	{
		// Normalize parameters
		firstWeekday %= 7;
		if (minDaysInFirstWeek < 1)
			minDaysInFirstWeek = 1;
		else if (minDaysInFirstWeek > 7)
			minDaysInFirstWeek = 7;

		// {@code object} might be in the first week of the next year, or last week of
		// the previous year, so we might have to try those too.
		int year = object.getYear();
		for (int refYear = year+1; refYear >= year-1; --refYear)
		{
			// {@code refDate} will always be in week 1
			LocalDate refDate = LocalDate.of(refYear, 1, minDaysInFirstWeek);
			// Go back to the start of {@code refDate}s week (i.e. day 1 of week 1)
			LocalDate weekStartDate = refDate.minusDays(ModAST.call(refDate.getDayOfWeek().getValue() - 1 - firstWeekday, 7));
			// Is our date {@code object} at or after day 1 of week 1?
			// (if not we have to calculate its week number based on the year before)
			if (!object.isBefore(weekStartDate))
			{
				long refDays = ChronoUnit.DAYS.between(weekStartDate, object);
				int refWeekDay = BoundLocalDateMethodWeekday.call(object);
				return new DateProto.Calendar(refYear, (int)(refDays/7+1), refWeekDay);
			}
		}
		// Can't happen
		return null;
	}

	public Object evaluate(BoundArguments args)
	{
		int firstWeekday = Utils.toInt(args.get(0));
		int minDaysInFirstWeek = Utils.toInt(args.get(1));
		return call(object, firstWeekday, minDaysInFirstWeek).asList();
	}
}
