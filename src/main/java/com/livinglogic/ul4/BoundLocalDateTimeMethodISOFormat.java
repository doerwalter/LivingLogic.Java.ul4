/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BoundLocalDateTimeMethodISOFormat extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodISOFormat(LocalDateTime object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "isoformat";
	}

	private static DateTimeFormatter formatter0 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
	private static DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.US);

	public static String call(LocalDateTime object)
	{
		DateTimeFormatter formatter;
		if (object.getNano() != 0)
			formatter = formatter1;
		else
			formatter = formatter0;
		return object.format(formatter);
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
