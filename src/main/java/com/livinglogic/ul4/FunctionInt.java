/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.commons.lang.math.NumberUtils;

public class FunctionInt implements Function
{
	public static int call()
	{
		return 0;
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return Integer.valueOf((String)obj);
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Long || obj instanceof BigInteger)
			return obj;
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
		else if (obj instanceof Float || obj instanceof Double)
			return ((Number)obj).intValue();
		else if (obj instanceof BigDecimal)
			return ((BigDecimal)obj).toBigInteger();
		throw new UnsupportedOperationException("int(" + Utils.objectType(obj) + ") not supported!");
	}

	public static Object call(Object obj1, Object obj2)
	{
		if (obj1 instanceof String)
		{
			if (obj2 instanceof Integer || obj2 instanceof Byte || obj2 instanceof Short || obj2 instanceof Long || obj2 instanceof BigInteger)
				return Integer.valueOf((String)obj1, ((Number)obj2).intValue());
		}
		throw new UnsupportedOperationException("int(" + Utils.objectType(obj1) + ", " + Utils.objectType(obj2) + ") not supported!");
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return call();
		else if (args.length == 1)
			return call(args[0]);
		else if (args.length == 2)
			return call(args[0], args[1]);
		throw new ArgumentCountMismatchException("function", "int", args.length, 0, 2);
	}

	public String getName()
	{
		return "int";
	}
}
