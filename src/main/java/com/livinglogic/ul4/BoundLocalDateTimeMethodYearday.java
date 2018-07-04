/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;

public class BoundLocalDateTimeMethodYearday extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodYearday(LocalDateTime object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "datetime.yearday";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getDayOfYear();
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
