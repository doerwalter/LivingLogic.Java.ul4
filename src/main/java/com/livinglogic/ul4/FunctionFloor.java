/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;


public class FunctionFloor extends Function
{
	@Override
	public String getNameUL4()
	{
		return "floor";
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

	public static Object call(int x, int digits)
	{
		if (digits >= 0)
			return x;
		else // digits < 0
		{
			int orgX = x;
			try
			{
				for (int i = 0; i < -digits; ++i)
					x = Math.floorDiv(x, 10);
				for (int i = 0; i < -digits; ++i)
					x = Math.multiplyExact(x, 10);
			}
			catch (ArithmeticException ex)
			{
				return call((long)orgX, digits);
			}
			return x;
		}
	}

	public static Object call(long x, int digits)
	{
		if (digits >= 0)
			return x;
		else // digits < 0
		{
			long orgX = x;
			try
			{
				for (int i = 0; i < -digits; ++i)
					x = Math.floorDiv(x, 10);
				for (int i = 0; i < -digits; ++i)
					x = Math.multiplyExact(x, 10);
			}
			catch (ArithmeticException ex)
			{
				return call(Utils.toBigInteger(orgX), digits);
			}
			return x;
		}
	}

	public static Object call(double x, int digits)
	{
		if (digits == 0)
		{
			x = Math.floor(x);
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
			x = Math.floor(x);
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
			x = Math.floor(x);
			for (int i = 0; i < digits; ++i)
				x /= 10.;
			return x;
		}
	}

	public static BigInteger call(BigInteger x, int digits)
	{
		if (digits >= 0)
			return x;
		BigInteger offset = Utils.powerOfTen(-digits);
		if (x.signum() < 0)
			x = x.subtract(new BigInteger(StringUtils.repeat("9", -digits)));
		return x.divide(offset).multiply(offset);
	}

	public static Object call(BigDecimal x, int digits)
	{
		if (digits <= 0)
			return call(x.toBigInteger(), digits);
		if (digits != 0)
			x = x.movePointRight(digits);
		if (x.signum() < 0)
			x = x.add(new BigDecimal("-0.9"));
		return new BigDecimal(x.toBigInteger().toString()).movePointLeft(digits);
	}

	public static Object call(Object x, int digits)
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
		throw new ArgumentTypeMismatchException("floor({!t}, {!t}) not supported", x, digits);
	}

	public static Object call(Object x, Object digits)
	{
		return call(x, Utils.toInt(digits));
	}

	public static final Function function = new FunctionFloor();
}
