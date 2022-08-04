/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigInteger;

public class FunctionOct extends Function
{
	@Override
	public String getNameUL4()
	{
		return "oct";
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
		return call(args.get(0));
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
		{
			int value = ((Number)obj).intValue();
			if (value < 0)
				return "-0o" + Integer.toOctalString(-value);
			else
				return "0o" + Integer.toOctalString(value);
		}
		else if (obj instanceof Boolean)
		{
			return ((Boolean)obj).booleanValue() ? "0o1" : "0o0";
		}
		else if (obj instanceof Long)
		{
			long value = ((Long)obj).longValue();
			if (value < 0)
				return "-0o" + Long.toOctalString(-value);
			else
				return "0o" + Long.toOctalString(value);
		}
		else if (obj instanceof BigInteger)
		{
			BigInteger bi = (BigInteger)obj;
			if (bi.signum() < 0)
			{
				return "-0o" + bi.toString(8).substring(1);
			}
			else
				return "0o" + bi.toString(8);
		}
		throw new ArgumentTypeMismatchException("oct({!t}) not supported", obj);
	}

	public static final Function function = new FunctionOct();
}
