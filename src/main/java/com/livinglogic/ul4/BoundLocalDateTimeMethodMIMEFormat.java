/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BoundLocalDateTimeMethodMIMEFormat extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodMIMEFormat(LocalDateTime object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "datetime.mimeformat";
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

	public static String call(LocalDateTime obj)
	{
		return obj.format(formatter);
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
