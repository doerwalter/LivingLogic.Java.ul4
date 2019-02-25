/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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

	public String nameUL4()
	{
		return "datetime.minute";
	}

	public static int call(LocalDateTime obj)
	{
		return obj.getMinute();
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
