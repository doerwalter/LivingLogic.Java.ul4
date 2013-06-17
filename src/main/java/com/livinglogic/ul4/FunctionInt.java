/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang.math.NumberUtils;

public class FunctionInt extends Function
{
	public String nameUL4()
	{
		return "int";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"obj", 0,
			"base", null
		);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0], args[1]);
	}

	public static int call()
	{
		return 0;
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
		{
			try
			{
				return Integer.valueOf((String)obj);
			}
			catch (NumberFormatException ex1)
			{
				try
				{
					return Long.valueOf((String)obj);
				}
				catch (NumberFormatException ex2)
				{
					return new BigInteger((String)obj);
				}
			}
		}
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Long)
			return obj;
		else if (obj instanceof BigInteger)
			return Utils.narrowBigInteger((BigInteger)obj);
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
		else if (obj instanceof Float || obj instanceof Double)
			return ((Number)obj).intValue();
		else if (obj instanceof BigDecimal)
			return ((BigDecimal)obj).toBigInteger();
		throw new ArgumentTypeMismatchException("int({})", obj);
	}

	public static Object call(Object obj1, Object obj2)
	{
		if (obj2 == null)
			return call(obj1);
		else if (obj1 instanceof String)
		{
			if (obj2 instanceof Integer || obj2 instanceof Byte || obj2 instanceof Short || obj2 instanceof Long || obj2 instanceof BigInteger)
				return Integer.valueOf((String)obj1, ((Number)obj2).intValue());
		}
		throw new ArgumentTypeMismatchException("int({}, {})", obj1, obj2);
	}
}
