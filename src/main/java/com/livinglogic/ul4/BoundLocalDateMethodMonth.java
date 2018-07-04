/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundLocalDateMethodMonth extends BoundMethod<LocalDate>
{
	public BoundLocalDateMethodMonth(LocalDate object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.month";
	}

	public static int call(LocalDate obj)
	{
		return obj.getMonthValue();
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
