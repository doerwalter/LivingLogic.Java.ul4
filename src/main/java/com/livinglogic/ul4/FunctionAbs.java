/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FunctionAbs implements Function
{
	public String getName()
	{
		return "abs";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "abs", args.length, 1);
	}

	public static Object call(Object arg)
	{
		if (arg instanceof Integer)
		{
			int value = ((Integer)arg).intValue();
			if (value >= 0)
				return arg;
			else if (value == -0x80000000) // Prevent overflow by switching to long
				return 0x80000000L;
			else
				return -value;
		}
		else if (arg instanceof Long)
		{
			long value = ((Long)arg).longValue();
			if (value >= 0)
				return arg;
			else if (value == -0x8000000000000000L) // Prevent overflow by switching to BigInteger
				return new BigInteger("8000000000000000", 16);
			else
				return -value;
		}
		else if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1 : 0;
		else if (arg instanceof Byte || arg instanceof Short)
		{
			int value = ((Number)arg).intValue();
			if (value >= 0)
				return arg;
			else
				return -value;
		}
		else if (arg instanceof Float)
		{
			float value = ((Float)arg).floatValue();
			if (value >= 0)
				return arg;
			else
				return -value;
		}
		else if (arg instanceof Double)
		{
			double value = ((Double)arg).doubleValue();
			if (value >= 0)
				return arg;
			else
				return -value;
		}
		else if (arg instanceof BigInteger)
			return ((BigInteger)arg).abs();
		else if (arg instanceof BigDecimal)
			return ((BigDecimal)arg).abs();
		throw new ArgumentTypeMismatchException("abs({})", arg);
	}
}
