/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class MethodLower extends NormalMethod
{
	public String nameUL4()
	{
		return "lower";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj);
	}

	public static Object call(String obj)
	{
		return obj.toLowerCase();
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("{}.lower()", obj);
	}
}
