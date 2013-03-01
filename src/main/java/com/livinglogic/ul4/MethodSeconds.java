/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class MethodSeconds extends NormalMethod
{
	public String nameUL4()
	{
		return "seconds";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj);
	}

	public static int call(TimeDelta obj)
	{
		return obj.getSeconds();
	}

	public static int call(Object obj)
	{
		if (obj instanceof TimeDelta)
			return call((TimeDelta)obj);
		throw new ArgumentTypeMismatchException("{}.seconds{}", obj);
	}
}
