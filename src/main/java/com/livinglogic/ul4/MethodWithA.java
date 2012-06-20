/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodWithA implements Method
{
	public String getName()
	{
		return "witha";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 1:
				return call(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "witha", args.length, 1);
		}
	}

	public static Color call(Color obj, int a)
	{
		return obj.witha(a);
	}

	public static Color call(Object obj, Object a)
	{
		if (obj instanceof Color)
			return call((Color)obj, Utils.toInt(a));
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".witha(" + Utils.objectType(a) + ") not supported!");
	}
}
