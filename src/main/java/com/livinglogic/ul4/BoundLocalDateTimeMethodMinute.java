/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;

public class BoundLocalDateTimeMethodMinute extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodMinute(LocalDateTime object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "minute";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getMinute();
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
