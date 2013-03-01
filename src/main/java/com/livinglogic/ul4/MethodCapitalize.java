/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class MethodCapitalize extends NormalMethod
{
	public String nameUL4()
	{
		return "capitalize";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj);
	}

	public static Object call(String obj)
	{
		return String.valueOf(Character.toTitleCase(obj.charAt(0))) + obj.substring(1).toLowerCase();
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("{}.capitalize()", obj);
	}
}
