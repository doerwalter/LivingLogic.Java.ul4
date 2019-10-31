/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;

public class BoundLocalDateTimeMethodMicrosecond extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodMicrosecond(LocalDateTime object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "microsecond";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getNano()/1000;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
