/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionRGB extends Function
{
	@Override
	public String getNameUL4()
	{
		return "rgb";
	}

	private static final Signature signature = new Signature().addBoth("r").addBoth("g").addBoth("b").addBoth("a", 1.0);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0), args.get(1), args.get(2), args.get(3));
	}

	public static Color call(Object arg1, Object arg2, Object arg3)
	{
		return Color.fromrgb(Utils.toDouble(arg1), Utils.toDouble(arg2), Utils.toDouble(arg3));
	}

	public static Color call(Object arg1, Object arg2, Object arg3, Object arg4)
	{
		return Color.fromrgb(Utils.toDouble(arg1), Utils.toDouble(arg2), Utils.toDouble(arg3), Utils.toDouble(arg4));
	}

	public static final Function function = new FunctionRGB();
}
