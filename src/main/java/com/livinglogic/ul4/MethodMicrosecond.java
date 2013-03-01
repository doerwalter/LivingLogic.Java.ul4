/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MethodMicrosecond extends NormalMethod
{
	public String nameUL4()
	{
		return "microsecond";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj);
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.MILLISECOND)*1000;
	}

	public static int call(Object obj)
	{
		if (obj instanceof Date)
			return call((Date)obj);
		throw new ArgumentTypeMismatchException("{}.microsecond()", obj);
	}
}
