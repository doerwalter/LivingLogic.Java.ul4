/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionOrd implements Function
{
	public String getName()
	{
		return "ord";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "ord", args.length, 1);
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
