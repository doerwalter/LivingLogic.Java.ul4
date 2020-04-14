/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundDateMethodDay extends BoundMethod<Date>
{
	public BoundDateMethodDay(Date object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "day";
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
