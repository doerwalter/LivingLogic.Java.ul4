/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;
import static java.util.Arrays.asList;

public class BoundLocalDateTimeMethodCalendar extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodCalendar(LocalDateTime object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "calendar";
	}

	private static final Signature signature = new Signature("firstweekday", 0, "mindaysinfirstweek", 4);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static DateProto.Calendar call(LocalDateTime object, int firstWeekday, int minDaysInFirstWeek)
	{
		return BoundLocalDateMethodCalendar.call(object.toLocalDate(), firstWeekday, minDaysInFirstWeek);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		int firstWeekday = Utils.toInt(args.get(0));
		int minDaysInFirstWeek = Utils.toInt(args.get(1));
		return call(object, firstWeekday, minDaysInFirstWeek).asList();
	}
}
