/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;

public class DateProto extends Proto
{
	public static Proto proto = new DateProto();

	public static String name = "date";

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public boolean bool(Object object)
	{
		return bool((Date)object);
	}

	public static boolean bool(Date object)
	{
		return object != null;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		return getAttr((Date)object, key);
	}

	public static Object getAttr(Date object, String key)
	{
		switch (key)
		{
			case "year":
				return new BoundDateMethodYear(object);
			case "month":
				return new BoundDateMethodMonth(object);
			case "day":
				return new BoundDateMethodDay(object);
			case "hour":
				return new BoundDateMethodHour(object);
			case "minute":
				return new BoundDateMethodMinute(object);
			case "second":
				return new BoundDateMethodSecond(object);
			case "microsecond":
				return new BoundDateMethodMicrosecond(object);
			case "weekday":
				return new BoundDateMethodWeekday(object);
			case "yearday":
				return new BoundDateMethodYearday(object);
			case "week":
				return new BoundDateMethodWeek(object);
			case "isoformat":
				return new BoundDateMethodISOFormat(object);
			case "mimeformat":
				return new BoundDateMethodMIMEFormat(object);
			default:
				throw new AttributeException(object, key);
		}
	}
}
