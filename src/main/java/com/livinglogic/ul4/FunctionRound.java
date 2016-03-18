/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class FunctionRound extends Function
{
	public String nameUL4()
	{
		return "round";
	}

	private static final Signature signature = new Signature("x", Signature.required, "digits", 0);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0), args.get(1));
	}

	public static Object call(Object x, Object digits)
	{
		int intDigits = Utils.toInt(digits);

		if (x instanceof Byte || x instanceof Short || x instanceof Integer)
		{
			if (intDigits >= 0)
				return x;
			else // intDigits < 0
			{
				int value = ((Number)x).intValue();
				for (int i = 0; i < -intDigits; ++i)
				{
					if (i == -intDigits -1)
						value += value < 0 ? -5 : 5;
					value /= 10;
				}
				for (int i = 0; i < -intDigits; ++i)
					value *= 10;
				return value;
			}
		}
		else if (x instanceof Long)
		{
			if (intDigits >= 0)
				return x;
			else // intDigits < 0
			{
				long value = ((Number)x).longValue();
				for (int i = 0; i < -intDigits; ++i)
				{
					if (i == -intDigits -1)
						value += value < 0 ? -5 : 5;
					value /= 10;
				}
				for (int i = 0; i < -intDigits; ++i)
					value *= 10;
				return value;
			}
		}
		else if (x instanceof Float)
		{
			float value = ((Number)x).floatValue();
			if (intDigits == 0)
			{
				value = Math.round(value);
				if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE)
					return (int)value;
				else if (Long.MIN_VALUE <= value && value <= Long.MAX_VALUE)
					return (long)value;
				else
					return new BigDecimal(value).toBigInteger();
			}
			else if (intDigits < 0)
			{
				for (int i = 0; i < -intDigits; ++i)
					value /= 10.;
				value = Math.round(value);
				for (int i = 0; i < -intDigits; ++i)
					value *= 10.;
				if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE)
					return (int)value;
				else if (Long.MIN_VALUE <= value && value <= Long.MAX_VALUE)
					return (long)value;
				else
					return new BigDecimal(value).toBigInteger();
			}
			else // intDigits > 0
			{
				for (int i = 0; i < intDigits; ++i)
					value *= 10.;
				value = Math.round(value);
				for (int i = 0; i < intDigits; ++i)
					value /= 10.;
				return value;
			}
		}
		else if (x instanceof Double)
		{
			double value = ((Number)x).doubleValue();
			if (intDigits == 0)
			{
				value = Math.round(value);
				if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE)
					return (int)value;
				else if (Long.MIN_VALUE <= value && value <= Long.MAX_VALUE)
					return (long)value;
				else
					return new BigDecimal(value).toBigInteger();
			}
			else if (intDigits < 0)
			{
				for (int i = 0; i < -intDigits; ++i)
					value /= 10.;
				value = Math.round(value);
				for (int i = 0; i < -intDigits; ++i)
					value *= 10.;
				if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE)
					return (int)value;
				else if (Long.MIN_VALUE <= value && value <= Long.MAX_VALUE)
					return (long)value;
				else
					return new BigDecimal(value).toBigInteger();
			}
			else // intDigits > 0
			{
				for (int i = 0; i < intDigits; ++i)
					value *= 10.;
				value = Math.round(value);
				for (int i = 0; i < intDigits; ++i)
					value /= 10.;
				return value;
			}
		}
		else if (x instanceof BigInteger)
		{
			if (intDigits >= 0)
				return x;
			BigInteger offset = Utils.powerOfTen(-intDigits-1);
			int signum = ((BigInteger)x).signum();
			return ((BigInteger)x).divide(offset).add(new BigInteger(signum >= 0 ? "5" : "-5")).divide(BigInteger.TEN).multiply(offset).multiply(BigInteger.TEN);
		}
		else if (x instanceof BigDecimal)
		{
			int signum = ((BigDecimal)x).signum();
			BigDecimal decValue = (BigDecimal)x;
			if (intDigits != 0)
				decValue = decValue.movePointRight(intDigits);
			decValue = decValue.add(new BigDecimal(signum >= 0 ? "0.5" : "-0.5"));
			if (intDigits <= 0)
			{
				BigInteger intValue = decValue.toBigInteger();
				if (intDigits < 0)
					intValue = intValue.multiply(Utils.powerOfTen(-intDigits));
				return intValue;
			}
			else
				return new BigDecimal(decValue.toBigInteger().toString()).movePointLeft(intDigits);
		}
		throw new ArgumentTypeMismatchException("round({}, {})", x, digits);
	}
}
