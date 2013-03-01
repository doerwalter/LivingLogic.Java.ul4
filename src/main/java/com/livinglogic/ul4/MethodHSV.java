/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Vector;

public class MethodHSV extends NormalMethod
{
	public String nameUL4()
	{
		return "hsv";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj);
	}

	public static Vector call(Color obj)
	{
		return obj.hsv();
	}

	public static Vector call(Object obj)
	{
		if (obj instanceof Color)
			return call((Color)obj);
		throw new ArgumentTypeMismatchException("{}.hsv()", obj);
	}
}
