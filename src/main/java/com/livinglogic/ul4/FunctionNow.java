/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;

public class FunctionNow extends Function
{
	public String nameUL4()
	{
		return "now";
	}

	public Object evaluate(BoundArguments args)
	{
		return call();
	}

	public static LocalDateTime call()
	{
		return LocalDateTime.now();
	}
}
