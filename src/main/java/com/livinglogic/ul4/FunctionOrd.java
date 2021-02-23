/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionOrd extends Function
{
	@Override
	public String getNameUL4()
	{
		return "ord";
	}

	private static final Signature signature = new Signature("c", Signature.required);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static int call(String obj)
	{
		if (obj.length() != 1)
		{
			throw new IllegalArgumentException(Utils.formatMessage("String {!r} contains more than one unicode character!", obj));
		}
		return (int)obj.charAt(0);
	}

	public static int call(Object obj)
	{
		if (obj instanceof String)
		{
			return call((String)obj);
		}
		throw new ArgumentTypeMismatchException("ord({!t}) not supported", obj);
	}

	public static Function function = new FunctionOrd();
}
