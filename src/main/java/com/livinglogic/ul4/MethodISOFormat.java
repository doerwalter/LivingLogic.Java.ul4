/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MethodISOFormat implements Method
{
	public String getName()
	{
		return "isoformat";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(obj);
			default:
				throw new ArgumentCountMismatchException("method", "isoformat", args.length, 0);
		}
	}

	private static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat isoDateTime2Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static SimpleDateFormat isoTimestampMicroFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'000'");

	public static String call(Date obj)
	{
		if (MethodMicrosecond.call(obj) != 0)
			return isoTimestampMicroFormatter.format(obj);
		else
		{
			if (MethodHour.call(obj) != 0 || MethodMinute.call(obj) != 0 || MethodSecond.call(obj) != 0)
				return isoDateTime2Formatter.format(obj);
			else
				return isoDateFormatter.format(obj);
		}
	}

	public static String call(Object obj)
	{
		if (obj instanceof Date)
			return call((Date)obj);
		throw new ArgumentTypeMismatchException("{}.isoformat()", obj);
	}
}
