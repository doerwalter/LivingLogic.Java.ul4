/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;

public class FunctionFloat extends Function
{
	public String nameUL4()
	{
		return "float";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"obj", 0.0
		);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
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
		throw new ArgumentTypeMismatchException("float({})", obj);
	}
}
