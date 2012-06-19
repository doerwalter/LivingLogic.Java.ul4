/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class FunctionUTCNow implements Function
{
	public String getName()
	{
		return "utcnow";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return call();
		throw new ArgumentCountMismatchException("function", "utcnow", args.length, 0);
	}

	public static Date call()
	{
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String formatted = df.format(new Date());
		df.setTimeZone(TimeZone.getDefault());
		try
		{
			return df.parse(formatted);
		}
		catch (ParseException ex)
		{
			// Can't happen
			return null;
		}
	}
}
