/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodB implements Method
{
	public String getName()
	{
		return "b";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		if (args.length == 0)
			return call(obj);
		throw new ArgumentCountMismatchException("method", "b", args.length, 0);
	}

	public static int call(Color obj)
	{
		return obj.getB();
	}

	public static int call(Object obj)
	{
		if (obj instanceof Color)
			return call((Color)obj);
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".b() not supported!");
	}
}
