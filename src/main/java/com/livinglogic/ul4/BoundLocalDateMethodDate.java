/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;

public class BoundLocalDateMethodDate extends BoundMethod<LocalDate>
{
	public BoundLocalDateMethodDate(LocalDate object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "date";
	}

	public static LocalDate call(EvaluationContext context, LocalDate obj)
	{
		return obj;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object);
	}
}
