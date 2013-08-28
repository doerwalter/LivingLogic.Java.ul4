/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.util.Map;

public class BoundDateMethodWeek extends BoundMethod<Date>
{
	private static Signature signature = new Signature("week", "firstweekday", null);

	public BoundDateMethodWeek(Date object)
	{
		super(object);
	}

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

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		return call(object, args[0] == null ? 0 : Utils.toInt(args[0]));
	}
}
