/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;

public class BoundLocalDateTimeMethodWeekday extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodWeekday(LocalDateTime object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "datetime.weekday";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getDayOfWeek().getValue()-1;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
