/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundLocalDateTimeMethodYear extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodYear(LocalDateTime object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "year";
	}

	public static int call(EvaluationContext context, LocalDateTime obj)
	{
		return obj.getYear();
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object);
	}
}
