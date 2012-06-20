/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodWithLum implements Method
{
	public String getName()
	{
		return "withlum";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 1:
				return call(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "withlum", args.length, 1);
		}
	}

	public static Color call(Color obj, double lum)
	{
		return obj.withlum(lum);
	}

	public static Color call(Object obj, Object lum)
	{
		if (obj instanceof Color)
			return call((Color)obj, Utils.toDouble(lum));
		throw new UnsupportedOperationException(Utils.objectType(obj) + ".withlum(" + Utils.objectType(lum) + ") not supported!");
	}
}
