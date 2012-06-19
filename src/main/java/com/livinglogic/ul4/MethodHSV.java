/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Vector;

public class MethodHSV implements Method
{
	public String getName()
	{
		return "hsv";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		if (args.length == 0)
			return call(obj);
		throw new ArgumentCountMismatchException("method", "hsv", args.length, 0);
	}

	public static Vector call(Color obj)
	{
		return obj.hsv();
	}

	public static Vector call(Object obj)
	{
		if (obj instanceof Color)
			return call((Color)obj);
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".hsv() not supported!");
	}
}
