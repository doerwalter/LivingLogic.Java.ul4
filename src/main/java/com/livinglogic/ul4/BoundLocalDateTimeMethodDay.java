/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
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

	@Override
	public String nameUL4()
	{
		return "day";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getDayOfMonth();
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
