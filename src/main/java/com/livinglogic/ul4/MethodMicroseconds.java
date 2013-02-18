/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodMicroseconds extends NormalMethod
{
	public String nameUL4()
	{
		return "microseconds";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj);
	}

	public static int call(TimeDelta obj)
	{
		return obj.getMicroseconds();
	}

	public static int call(Object obj)
	{
		if (obj instanceof TimeDelta)
			return call((TimeDelta)obj);
		throw new ArgumentTypeMismatchException("{}.microseconds{}", obj);
	}
}