/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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

	@Override
	public String nameUL4()
	{
		return "isoformat";
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);

	public static String call(LocalDate object)
	{
		return object.format(formatter);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
