/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;

public class BoundLocalDateMethodWeekday extends BoundMethod<LocalDate>
{
	public BoundLocalDateMethodWeekday(LocalDate object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.weekday";
	}

	public static int call(LocalDate obj)
	{
		return obj.getDayOfWeek().getValue()-1;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}