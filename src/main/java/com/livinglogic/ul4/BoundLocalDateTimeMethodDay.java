/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundLocalDateTimeMethodDay extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodDay(LocalDateTime object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "datetime.day";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getDayOfMonth();
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
