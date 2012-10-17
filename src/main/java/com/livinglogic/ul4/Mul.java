/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Mul extends Binary
{
	public Mul(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "mul";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static String call(int arg1, String arg2)
	{
		return StringUtils.repeat(arg2, arg1);
	}

	public static String call(long arg1, String arg2)
	{
		if (((int)arg1) != arg1)
			throw new ArgumentTypeMismatchException("{} * {}", arg1, arg2);
		return StringUtils.repeat(arg2, (int)arg1);
	}

	public static List call(int arg1, List arg2)
	{
		ArrayList result = new ArrayList();

		for (;arg1>0;--arg1)
			result.addAll(arg2);
		return result;
	}

	public static List call(long arg1, List arg2)
	{
		ArrayList result = new ArrayList();

		for (;arg1>0;--arg1)
			result.addAll(arg2);
		return result;
	}

	public static Object call(int arg1, int arg2)
	{
		if (arg1 == 0 || arg2 == 0)
			return 0;
		int result = arg1 * arg2;
		if (result/arg1 == arg2) // result doesn't seem to have overflowed
			return result;
		else // we had an overflow => promote to BigInteger
			return Utils.toBigInteger(arg1).multiply(Utils.toBigInteger(arg2));
	}

	public static Object call(long arg1, long arg2)
	{
		if (arg1 == 0 || arg2 == 0)
			return 0;
		long result = arg1 * arg2;
		if (result/arg1 == arg2) // result doesn't seem to have overflowed
			return result;
		else // we had an overflow => promote to BigInteger
			return Utils.toBigInteger(arg1).multiply(Utils.toBigInteger(arg2));
	}

	public static Object call(float arg1, float arg2)
	{
		// FIXME: Overflow check
		return arg1 * arg2;
	}

	public static Object call(double arg1, double arg2)
	{
		// FIXME: Overflow check
		return arg1 * arg2;
	}

	public static Object call(int arg1, TimeDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(long arg1, TimeDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(float arg1, TimeDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(double arg1, TimeDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(TimeDelta arg1, int arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(TimeDelta arg1, long arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(TimeDelta arg1, float arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(TimeDelta arg1, double arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(int arg1, MonthDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(long arg1, MonthDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(MonthDelta arg1, int arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(MonthDelta arg1, long arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(Utils.toInt(arg1), Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call(Utils.toLong(arg1), ((Long)arg2).longValue());
			else if (arg2 instanceof Float)
				return call(Utils.toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return call(Utils.toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof String)
				return call(Utils.toInt(arg1), (String)arg2);
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).multiply(Utils.toBigInteger(Utils.toInt(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof List)
				return call(Utils.toInt(arg1), (List)arg2);
			else if (arg2 instanceof TimeDelta)
				return call(Utils.toInt(arg1), (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call(Utils.toInt(arg1), (MonthDelta)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof Float)
				return call(Utils.toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return call(Utils.toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof String)
				return call(Utils.toInt(arg1), (String)arg2);
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).multiply(Utils.toBigInteger(Utils.toLong(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof List)
				return call(Utils.toLong(arg1), (List)arg2);
			else if (arg2 instanceof TimeDelta)
				return call(Utils.toLong(arg1), (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call(Utils.toLong(arg1), (MonthDelta)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return call(Utils.toFloat(arg1), Utils.toFloat(arg2));
			else if (arg2 instanceof Double)
				return call(Utils.toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof TimeDelta)
				return call(Utils.toFloat(arg1), (TimeDelta)arg2);
		}
		else if (arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return call(Utils.toDouble(arg1), Utils.toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof TimeDelta)
				return call(Utils.toDouble(arg1), (TimeDelta)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return ((BigInteger)arg1).multiply(Utils.toBigInteger(Utils.toInt(arg2)));
			else if (arg2 instanceof Long)
				return ((BigInteger)arg1).multiply(Utils.toBigInteger(Utils.toLong(arg2)));
			else if (arg2 instanceof Float)
				return new BigDecimal((BigInteger)arg1).multiply(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return new BigDecimal((BigInteger)arg1).multiply(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg1).multiply(((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal((BigInteger)arg1));
		}
		else if (arg1 instanceof BigDecimal)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return ((BigDecimal)arg1).multiply(new BigDecimal(Utils.toDouble(arg2)));
			else if (arg2 instanceof BigInteger)
				return ((BigDecimal)arg1).multiply(new BigDecimal(((BigInteger)arg2)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg1).multiply(((BigDecimal)arg2));
		}
		else if (arg1 instanceof String)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(Utils.toInt(arg2), (String)arg1);
			else if (arg2 instanceof Long)
				return call(Utils.toLong(arg2), (String)arg1);
		}
		else if (arg1 instanceof List)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(Utils.toInt(arg2), (List)arg1);
			else if (arg2 instanceof Long)
				return call(Utils.toLong(arg2), (List)arg1);
		}
		else if (arg1 instanceof TimeDelta)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call((TimeDelta)arg1, Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call((TimeDelta)arg1, Utils.toLong(arg2));
			else if (arg2 instanceof Float || arg2 instanceof Double)
				return call((TimeDelta)arg1, Utils.toDouble(arg2));
		}
		else if (arg1 instanceof MonthDelta)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call((MonthDelta)arg1, Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call((MonthDelta)arg1, Utils.toLong(arg2));
		}
		throw new ArgumentTypeMismatchException("{} * {}", arg1, arg2);
	}
}
