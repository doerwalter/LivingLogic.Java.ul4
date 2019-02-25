/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;

public class FunctionToday extends Function
{
	public String nameUL4()
	{
		return "today";
	}

	public Object evaluate(BoundArguments args)
	{
		return call();
	}

	public static LocalDate call()
	{
		return LocalDate.now();
	}
}
