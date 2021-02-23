/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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

	@Override
	public String getNameUL4()
	{
		return "weekday";
	}

	public static int call(LocalDate obj)
	{
		return obj.getDayOfWeek().getValue()-1;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
