/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;

public class FunctionFloat extends Function
{
	@Override
	public String getNameUL4()
	{
		return "float";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj", 0.0);

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

	public static Object call()
	{
		return 0.0;
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return Double.valueOf((String)obj);
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
			return (double)((Number)obj).intValue();
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? 1.0d : 0.0d;
		else if (obj instanceof Long)
			return (double)((Long)obj).longValue();
		else if (obj instanceof BigInteger)
			return new BigDecimal(((BigInteger)obj).toString());
		else if (obj instanceof BigDecimal || obj instanceof Float || obj instanceof Double)
			return obj;
		throw new ArgumentTypeMismatchException("float({!t}) not supported", obj);
	}
}
