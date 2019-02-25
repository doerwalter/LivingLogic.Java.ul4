/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundDateMethodYearday extends BoundMethod<Date>
{
	public BoundDateMethodYearday(Date object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.yearday";
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
