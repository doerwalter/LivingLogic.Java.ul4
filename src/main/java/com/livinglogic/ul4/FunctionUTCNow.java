/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;
import java.time.Clock;

public class FunctionUTCNow extends Function
{
	public String nameUL4()
	{
		return "utcnow";
	}

	public Object evaluate(BoundArguments args)
	{
		return call();
	}

	public static LocalDateTime call()
	{
		return LocalDateTime.now(Clock.systemUTC());
	}
}
