/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Date;
import java.util.Map;

public class BoundDateMethodWeek extends BoundMethod<Date>
{
	public BoundDateMethodWeek(Date object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.week";
	}

	private static final Signature signature = new Signature("firstweekday", null);

	public Signature getSignature()
	{
		return signature;
	}

	public static int call(Date object, int firstWeekday)
	{
		int yearday = BoundDateMethodYearday.call(object)+6;
		int jan1Weekday = BoundDateMethodWeekday.call(FunctionDate.call(BoundDateMethodYear.call(object), 1, 1));
		while (jan1Weekday != firstWeekday)
		{
			--yearday;
			jan1Weekday = (++jan1Weekday) % 7;
		}
		return yearday/7;
	}

	public Object evaluate(BoundArguments args)
	{
		Object arg = args.get(0);
		return call(object, arg == null ? 0 : Utils.toInt(arg));
	}
}
