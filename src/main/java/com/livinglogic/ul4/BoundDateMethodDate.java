/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class BoundDateMethodDate extends BoundMethod<Date>
{
	public BoundDateMethodDate(Date object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "date";
	}

	public static LocalDate call(Date obj)
	{
		return obj.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
