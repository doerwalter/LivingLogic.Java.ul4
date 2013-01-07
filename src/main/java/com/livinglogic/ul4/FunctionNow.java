/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Date;

public class FunctionNow extends NormalFunction
{
	public String getName()
	{
		return "now";
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call();
	}

	public static Date call()
	{
		return new Date();
	}
}
