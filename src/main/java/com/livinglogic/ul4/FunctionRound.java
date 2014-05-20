/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class FunctionRound extends Function
{
	public String nameUL4()
	{
		return "round";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"x", Signature.required,
			"digits", 0
		);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0], args[1]);
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
					value /= 10;
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
					value /= 10;
				for (int i = 0; i < -intDigits; ++i)
					value *= 10;
				return value;
			}
		}
		else if (x instanceof Float)
		{
			float value = ((Number)x).floatValue();
			if (intDigits == 0)
				return (int)Math.round(value);
			else if (intDigits < 0)
			{
				for (int i = 0; i < -intDigits; ++i)
					value /= 10.;
				value = Math.round(value);
				for (int i = 0; i < -intDigits; ++i)
					value *= 10.;
				return (int)value;
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
				return (int)Math.round(value);
			else if (intDigits < 0)
			{
				for (int i = 0; i < -intDigits; ++i)
					value /= 10.;
				value = Math.round(value);
				for (int i = 0; i < -intDigits; ++i)
					value *= 10.;
				return (int)value;
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
		else if (x instanceof BigDecimal)
		{
			BigDecimal result = ((BigDecimal)x).round(new MathContext(intDigits));
			return (intDigits <= 0) ? result.toBigInteger() : result;
		}
		throw new ArgumentTypeMismatchException("round({}, {})", x, digits);
	}
}
