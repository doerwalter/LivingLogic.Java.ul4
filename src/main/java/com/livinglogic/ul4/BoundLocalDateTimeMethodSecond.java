/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;

public class BoundLocalDateTimeMethodSecond extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodSecond(LocalDateTime object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "second";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getSecond();
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
