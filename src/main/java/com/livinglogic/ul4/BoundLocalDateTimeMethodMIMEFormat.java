/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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

	@Override
	public String getNameUL4()
	{
		return "mimeformat";
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

	public static String call(LocalDateTime obj)
	{
		return obj.format(formatter);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
