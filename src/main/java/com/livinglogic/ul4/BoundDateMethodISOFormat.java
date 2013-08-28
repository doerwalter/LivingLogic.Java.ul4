/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BoundDateMethodISOFormat extends BoundMethod<Date>
{
	private static Signature signature = new Signature("isoformat");

	public BoundDateMethodISOFormat(Date object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	private static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat isoDateTime2Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static SimpleDateFormat isoTimestampMicroFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'000'");

	public static String call(Date object)
	{
		if (BoundDateMethodMicrosecond.call(object) != 0)
			return isoTimestampMicroFormatter.format(object);
		else
		{
			if (BoundDateMethodHour.call(object) != 0 || BoundDateMethodMinute.call(object) != 0 || BoundDateMethodSecond.call(object) != 0)
				return isoDateTime2Formatter.format(object);
			else
				return isoDateFormatter.format(object);
		}
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		return call(object);
	}
}
