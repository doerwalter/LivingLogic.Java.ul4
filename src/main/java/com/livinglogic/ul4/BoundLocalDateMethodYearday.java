/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;

public class BoundLocalDateMethodYearday extends BoundMethod<LocalDate>
{
	public BoundLocalDateMethodYearday(LocalDate object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "yearday";
	}

	public static int call(LocalDate obj)
	{
		return obj.getDayOfYear();
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
