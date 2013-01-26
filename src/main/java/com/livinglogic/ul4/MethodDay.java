/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MethodDay extends NormalMethod
{
	public String nameUL4()
	{
		return "day";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj);
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static int call(Object obj)
	{
		if (obj instanceof Date)
			return call((Date)obj);
		throw new ArgumentTypeMismatchException("{}.day{}", obj);
	}
}
