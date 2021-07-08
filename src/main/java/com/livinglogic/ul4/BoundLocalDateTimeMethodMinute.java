/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
	public String getNameUL4()
	{
		return "minute";
	}

	public static int call(EvaluationContext context, LocalDateTime obj)
	{
		return obj.getMinute();
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object);
	}
}
