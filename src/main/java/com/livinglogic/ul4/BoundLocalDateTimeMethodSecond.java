/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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

	public String nameUL4()
	{
		return "second";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getSecond();
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
