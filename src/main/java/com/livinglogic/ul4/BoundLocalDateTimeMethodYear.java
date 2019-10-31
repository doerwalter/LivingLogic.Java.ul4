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

public class BoundLocalDateTimeMethodYear extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodYear(LocalDateTime object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "year";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getYear();
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
