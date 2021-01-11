/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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

	private static final Signature signature = new Signature("dump", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static Object call(String obj)
	{
		return com.livinglogic.ul4on.Utils.loads(obj, null);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("fromul4on({!t}) not supported", obj);
	}
}
