/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionRGB extends Function
{
	public String nameUL4()
	{
		return "rgb";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"r", Signature.required,
			"g", Signature.required,
			"b", Signature.required,
			"a", 1.0
		);
	}

	public Object evaluate(Object[] args)
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
