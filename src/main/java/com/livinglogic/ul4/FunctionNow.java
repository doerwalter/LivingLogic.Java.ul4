/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;

public class FunctionNow extends Function
{
	@Override
	public String getNameUL4()
	{
		return "now";
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call();
	}

	public static LocalDateTime call()
	{
		return LocalDateTime.now();
	}

	public static final Function function = new FunctionNow();
}
