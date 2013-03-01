/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class MethodValues extends NormalMethod
{
	public String nameUL4()
	{
		return "values";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj);
	}

	public static Object call(Map obj)
	{
		return obj.values();
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Map)
			return call((Map)obj);
		throw new ArgumentTypeMismatchException("{}.values()", obj);
	}
}
