/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodWithLum extends NormalMethod
{
	public String getName()
	{
		return "withlum";
	}

	protected void makeArgumentDescriptions(ArgumentDescriptions argumentDescriptions)
	{
		argumentDescriptions.add("lum");
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj, args[0]);
	}

	public static Color call(Color obj, double lum)
	{
		return obj.withlum(lum);
	}

	public static Color call(Object obj, Object lum)
	{
		if (obj instanceof Color)
			return call((Color)obj, Utils.toDouble(lum));
		throw new ArgumentTypeMismatchException("{}.withlum({})", obj, lum);
	}
}
