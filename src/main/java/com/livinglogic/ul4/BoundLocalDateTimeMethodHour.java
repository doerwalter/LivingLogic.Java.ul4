/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.time.LocalDateTime;
import java.util.Map;

public class BoundLocalDateTimeMethodHour extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodHour(LocalDateTime object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "hour";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getHour();
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
