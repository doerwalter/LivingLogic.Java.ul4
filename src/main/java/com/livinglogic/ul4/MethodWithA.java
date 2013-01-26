/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodWithA extends NormalMethod
{
	public String nameUL4()
	{
		return "witha";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("a");
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj, args[0]);
	}

	public static Color call(Color obj, int a)
	{
		return obj.witha(a);
	}

	public static Color call(Object obj, Object a)
	{
		if (obj instanceof Color)
			return call((Color)obj, Utils.toInt(a));
		throw new ArgumentTypeMismatchException("{}.witha({})", obj, a);
	}
}
