/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class FunctionBin implements Function
{
	public String getName()
	{
		return "bin";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "bin", args.length, 1);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
		{
			int value = ((Number)obj).intValue();
			if (value < 0)
				return "-0b" + Integer.toBinaryString(-value);
			else
				return "0b" + Integer.toBinaryString(value);
		}
		else if (obj instanceof Boolean)
		{
			return ((Boolean)obj).booleanValue() ? "0b1" : "0b0";
		}
		else if (obj instanceof Long)
		{
			long value = ((Long)obj).longValue();
			if (value < 0)
				return "-0b" + Long.toBinaryString(-value);
			else
				return "0b" + Long.toBinaryString(value);
		}
		else if (obj instanceof BigInteger)
		{
			BigInteger bi = (BigInteger)obj;
			if (bi.signum() < 0)
			{
				return "-0b" + bi.toString(2).substring(1);
			}
			else
				return "0b" + bi.toString(2);
		}
		throw new ArgumentTypeMismatchException("bin({})", obj);
	}
}
