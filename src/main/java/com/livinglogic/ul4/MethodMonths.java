/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class MethodMonths extends NormalMethod
{
	public String nameUL4()
	{
		return "months";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj);
	}

	public static int call(MonthDelta obj)
	{
		return obj.getMonths();
	}

	public static int call(Object obj)
	{
		if (obj instanceof MonthDelta)
			return call((MonthDelta)obj);
		throw new ArgumentTypeMismatchException("{}.months{}", obj);
	}
}
