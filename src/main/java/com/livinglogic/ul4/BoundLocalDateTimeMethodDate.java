/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class BoundLocalDateTimeMethodDate extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodDate(LocalDateTime object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "date";
	}

	public static LocalDate call(LocalDateTime obj)
	{
		return obj.toLocalDate();
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
