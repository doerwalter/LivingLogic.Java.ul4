/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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

	@Override
	public String getNameUL4()
	{
		return "yearday";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getDayOfYear();
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
