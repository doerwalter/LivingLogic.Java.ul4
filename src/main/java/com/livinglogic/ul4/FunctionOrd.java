/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionOrd extends Function
{
	public String nameUL4()
	{
		return "ord";
	}

	private static final Signature signature = new Signature("c", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
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
