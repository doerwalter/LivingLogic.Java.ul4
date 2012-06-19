/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MethodSecond implements Method
{
	public String getName()
	{
		return "second";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(obj);
			default:
				throw new ArgumentCountMismatchException("method", "second", args.length, 0);
		}
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.SECOND);
	}

	public static int call(Object obj)
	{
		if (obj instanceof Date)
			return call((Date)obj);
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".second() not supported!");
	}

}
