/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BoundLocalDateMethodISOFormat extends BoundMethod<LocalDate>
{
	public BoundLocalDateMethodISOFormat(LocalDate object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.isoformat";
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);

	public static String call(LocalDate object)
	{
		return object.format(formatter);
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
