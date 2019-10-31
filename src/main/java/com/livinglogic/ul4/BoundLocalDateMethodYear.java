/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundLocalDateMethodYear extends BoundMethod<LocalDate>
{
	public BoundLocalDateMethodYear(LocalDate object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "year";
	}

	public static int call(LocalDate obj)
	{
		return obj.getYear();
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
