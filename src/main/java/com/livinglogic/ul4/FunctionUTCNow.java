/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;
import java.time.Clock;

public class FunctionUTCNow extends Function
{
	@Override
	public String getNameUL4()
	{
		return "utcnow";
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call();
	}

	public static LocalDateTime call()
	{
		return LocalDateTime.now(Clock.systemUTC());
	}

	public static final Function function = new FunctionUTCNow();
}
