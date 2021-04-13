/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionHSV extends Function
{
	@Override
	public String getNameUL4()
	{
		return "hsv";
	}

	private static final Signature signature = new Signature("h", Signature.required, "s", Signature.required, "v", Signature.required, "a", 1.0);

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
		return Color.fromhsv(Utils.toDouble(arg1), Utils.toDouble(arg2), Utils.toDouble(arg3));
	}

	public static Color call(Object arg1, Object arg2, Object arg3, Object arg4)
	{
		return Color.fromhsv(Utils.toDouble(arg1), Utils.toDouble(arg2), Utils.toDouble(arg3), Utils.toDouble(arg4));
	}

	public static final Function function = new FunctionHSV();
}
