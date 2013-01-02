/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodR implements Method
{
	public String getName()
	{
		return "r";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		if (args.length == 0)
			return call(obj);
		throw new ArgumentCountMismatchException("method", "r", args.length, 0);
	}

	public static int call(Color obj)
	{
		return obj.getR();
	}

	public static int call(Object obj)
	{
		if (obj instanceof Color)
			return call((Color)obj);
		throw new ArgumentTypeMismatchException("{}.r()", obj);
	}
}
