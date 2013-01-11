/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionOrd extends NormalFunction
{
	public String getName()
	{
		return "ord";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("c");
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(args[0]);
	}

	public static int call(String obj)
	{
		if (obj.length() != 1)
		{
			throw new IllegalArgumentException("String " + obj + " contains more than one unicode character!");
		}
		return (int)obj.charAt(0);
	}

	public static int call(Object obj)
	{
		if (obj instanceof String)
		{
			return call((String)obj);
		}
		throw new ArgumentTypeMismatchException("ord({})", obj);
	}
}
