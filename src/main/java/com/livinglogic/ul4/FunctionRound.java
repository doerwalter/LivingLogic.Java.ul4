/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;


public class FunctionRound extends Function
{
	@Override
	public String getNameUL4()
	{
		return "round";
	}

	private static final Signature signature = new Signature().addPositionalOnly("number").addBoth("digits", 0);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(args.get(0), args.get(1));
	}

	public static int call(int x, int digits)
	{
		if (digits >= 0)
			return x;
		else // digits < 0
		{
			for (int i = 0; i < -digits; ++i)
			{
				if (i == -digits -1)
					x += x < 0 ? -5 : 5;
				x /= 10;
			}
			for (int i = 0; i < -digits; ++i)
				x *= 10;
			return x;
		}
	}

	public static long call(long x, int digits)
	{
		if (digits >= 0)
			return x;
		else // digits < 0
		{
			for (int i = 0; i < -digits; ++i)
			{
				if (i == -digits -1)
					x += x < 0 ? -5 : 5;
				x /= 10;
			}
			for (int i = 0; i < -digits; ++i)
				x *= 10;
			return x;
		}
	}

	public static Number call(double x, int digits)
	{
		if (digits == 0)
		{
			x = Math.round(x);
			if (Integer.MIN_VALUE <= x && x <= Integer.MAX_VALUE)
				return (int)x;
			else if (Long.MIN_VALUE <= x && x <= Long.MAX_VALUE)
				return (long)x;
			else
				return new BigDecimal(x).toBigInteger();
		}
		else if (digits < 0)
		{
			for (int i = 0; i < -digits; ++i)
				x /= 10.;
			x = Math.round(x);
			for (int i = 0; i < -digits; ++i)
				x *= 10.;
			if (Integer.MIN_VALUE <= x && x <= Integer.MAX_VALUE)
				return (int)x;
			else if (Long.MIN_VALUE <= x && x <= Long.MAX_VALUE)
				return (long)x;
			else
				return new BigDecimal(x).toBigInteger();
		}
		else // digits > 0
		{
			for (int i = 0; i < digits; ++i)
				x *= 10.;
			x = Math.round(x);
			for (int i = 0; i < digits; ++i)
				x /= 10.;
			return x;
		}
	}

	public static BigInteger call(BigInteger x, int digits)
	{
		if (digits >= 0)
			return x;
		BigInteger offset = Utils.powerOfTen(-digits-1);
		return x.divide(offset).add(new BigInteger(x.signum() >= 0 ? "5" : "-5")).divide(BigInteger.TEN).multiply(offset).multiply(BigInteger.TEN);
	}

	public static Number call(BigDecimal x, int digits)
	{
		if (digits != 0)
			x = x.movePointRight(digits);
		x = x.add(new BigDecimal(x.signum() >= 0 ? "0.5" : "-0.5"));
		if (digits <= 0)
		{
			BigInteger intValue = x.toBigInteger();
			if (digits < 0)
				intValue = intValue.multiply(Utils.powerOfTen(-digits));
			return intValue;
		}
		else
			return new BigDecimal(x.toBigInteger().toString()).movePointLeft(digits);
	}

	public static Number call(Object x, int digits)
	{
		if (x instanceof Byte || x instanceof Short || x instanceof Integer)
			return call(((Number)x).intValue(), digits);
		else if (x instanceof Long)
			return call(((Number)x).longValue(), digits);
		else if (x instanceof Float || x instanceof Double)
			return call(((Number)x).doubleValue(), digits);
		else if (x instanceof BigInteger)
			return call((BigInteger)x, digits);
		else if (x instanceof BigDecimal)
			return call((BigDecimal)x, digits);
		throw new ArgumentTypeMismatchException("round({!t}, {!t}) not supported", x, digits);
	}

	public static Number call(Object x, Object digits)
	{
		return call(x, Utils.toInt(digits));
	}

	public static final Function function = new FunctionRound();
}
