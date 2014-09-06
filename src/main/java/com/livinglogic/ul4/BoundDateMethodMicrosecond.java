/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundDateMethodMicrosecond extends BoundMethod<Date>
{
	public BoundDateMethodMicrosecond(Date object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.microsecond";
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.MILLISECOND)*1000;
	}

	public Object evaluate(Object[] args)
	{
		return call(object);
	}
}
