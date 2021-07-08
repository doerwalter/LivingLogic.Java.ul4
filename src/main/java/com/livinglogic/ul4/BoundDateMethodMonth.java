/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundDateMethodMonth extends BoundMethod<Date>
{
	public BoundDateMethodMonth(Date object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "month";
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.MONTH)+1;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(object);
	}
}
