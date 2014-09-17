/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FunctionUTCNow extends Function
{
	public String nameUL4()
	{
		return "utcnow";
	}

	public Object evaluate(List<Object> args)
	{
		return call();
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
