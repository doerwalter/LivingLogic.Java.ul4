/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionHLS extends Function
{
	public String nameUL4()
	{
		return "hls";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("h");
		signature.add("l");
		signature.add("s");
		signature.add("a", 1.0);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0], args[1], args[2], args[3]);
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
