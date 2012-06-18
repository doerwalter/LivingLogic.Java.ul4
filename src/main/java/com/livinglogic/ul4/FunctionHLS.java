/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionHLS implements Function
{
	public String getName()
	{
		return "hls";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 3)
			return call(args[0], args[1], args[2]);
		else if (args.length == 4)
			return call(args[0], args[1], args[2], args[3]);
		throw new ArgumentCountMismatchException("function", "hls", args.length, 3, 4);
	}

	public static Color call(Object arg1, Object arg2, Object arg3)
	{
		return Color.fromhls(Utils.toDouble(arg1), Utils.toDouble(arg2), Utils.toDouble(arg3));
	}

	public static Color call(Object arg1, Object arg2, Object arg3, Object arg4)
	{
		return Color.fromhls(Utils.toDouble(arg1), Utils.toDouble(arg2), Utils.toDouble(arg3), Utils.toDouble(arg4));
	}

}
