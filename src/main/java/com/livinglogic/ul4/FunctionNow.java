/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Date;

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

	public static Date call()
	{
		return new Date();
	}
}
