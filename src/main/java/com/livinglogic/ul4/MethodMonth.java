/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MethodMonth extends NormalMethod
{
	public String nameUL4()
	{
		return "month";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj);
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.MONTH)+1;
	}

	public static int call(Object obj)
	{
		if (obj instanceof Date)
			return call((Date)obj);
		throw new ArgumentTypeMismatchException("{}.month()", obj);
	}
}
