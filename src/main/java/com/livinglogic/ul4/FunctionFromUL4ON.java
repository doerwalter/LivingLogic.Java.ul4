/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionFromUL4ON extends Function
{
	public String nameUL4()
	{
		return "fromul4on";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("string");
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
	}

	public static Object call(String obj)
	{
		return com.livinglogic.ul4on.Utils.loads(obj);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("fromul4on({})", obj);
	}
}
