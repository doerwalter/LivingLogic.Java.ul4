/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionRGB extends NormalFunction
{
	public String getName()
	{
		return "rgb";
	}

	protected void makeArgumentDescriptions(ArgumentDescriptions argumentDescriptions)
	{
		argumentDescriptions.add("r");
		argumentDescriptions.add("g");
		argumentDescriptions.add("b");
		argumentDescriptions.add("a", 1.0);
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(args[0], args[1], args[2], args[3]);
	}

	public static Color call(Object arg1, Object arg2, Object arg3)
	{
		return Color.fromrgb(Utils.toDouble(arg1), Utils.toDouble(arg2), Utils.toDouble(arg3));
	}

	public static Color call(Object arg1, Object arg2, Object arg3, Object arg4)
	{
		return Color.fromrgb(Utils.toDouble(arg1), Utils.toDouble(arg2), Utils.toDouble(arg3), Utils.toDouble(arg4));
	}

}
