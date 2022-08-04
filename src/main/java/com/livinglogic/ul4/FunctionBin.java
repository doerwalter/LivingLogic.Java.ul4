/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

import java.math.BigInteger;

public class FunctionBin extends Function
{
	@Override
	public String getNameUL4()
	{
		return "bin";
	}

	private static final Signature signature = new Signature().addPositionalOnly("number");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0));
	}

	public static Object call(EvaluationContext context, Object obj)
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
		throw new ArgumentTypeMismatchException("bin({!t}) not supported", obj);
	}

	public static final Function function = new FunctionBin();
}
