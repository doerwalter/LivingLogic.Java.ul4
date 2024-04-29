/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;

public class FunctionToday extends Function
{
	@Override
	public String getNameUL4()
	{
		return "today";
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call();
	}

	public static LocalDate call()
	{
		return LocalDate.now();
	}

	public static final Function function = new FunctionToday();
}
