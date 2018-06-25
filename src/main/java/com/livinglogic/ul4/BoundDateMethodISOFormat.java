/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BoundDateMethodISOFormat extends BoundMethod<Date>
{
	public BoundDateMethodISOFormat(Date object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.isoformat";
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

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
