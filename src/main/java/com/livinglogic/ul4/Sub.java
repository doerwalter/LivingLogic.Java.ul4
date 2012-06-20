/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.math.BigInteger;
import java.math.BigDecimal;

class Sub extends Binary
{
	public Sub(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "sub";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(int arg1, int arg2)
	{
		int result = arg1 - arg2;
		if ((arg1 >= 0) == (arg2 >= 0)) // arguments have same sign, so there can be no overflow
			return result;
		else if ((arg1 >= 0) == (result >= 0)) // result didn't change sign, so there was no overflow
			return result;
		else // we had an overflow => promote to BigInteger
			return Utils.toBigInteger(arg1).add(Utils.toBigInteger(arg2).negate());
	}

	public static Object call(long arg1, long arg2)
	{
		long result = arg1 - arg2;
		if ((arg1 >= 0) == (arg2 >= 0)) // arguments have same sign, so there can be no overflow
			return result;
		else if ((arg1 >= 0) == (result >= 0)) // result didn't change sign, so there was no overflow
			return result;
		else // we had an overflow => promote to BigInteger
			return Utils.toBigInteger(arg1).add(Utils.toBigInteger(arg2).negate());
	}

	public static Object call(float arg1, float arg2)
	{
		return arg1 - arg2;
	}

	public static Object call(double arg1, double arg2)
	{
		return arg1 - arg2;
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
			else if (arg2 instanceof BigInteger)
				return Utils.toBigInteger(Utils.toInt(arg1)).subtract((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(Utils.toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof Float)
				return call(Utils.toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return call(Utils.toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof BigInteger)
				return Utils.toBigInteger(Utils.toLong(arg1)).subtract((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(Utils.toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return call(Utils.toFloat(arg1), Utils.toFloat(arg2));
			else if (arg2 instanceof Double)
				return call(Utils.toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(Utils.toDouble(arg1)).subtract(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(Utils.toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return call(Utils.toDouble(arg1), Utils.toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(Utils.toDouble(arg1)).subtract(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(Utils.toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return ((BigInteger)arg1).subtract(Utils.toBigInteger(Utils.toInt(arg2)));
			else if (arg2 instanceof Long)
				return ((BigInteger)arg1).subtract(Utils.toBigInteger(Utils.toLong(arg2)));
			else if (arg2 instanceof Float)
				return new BigDecimal((BigInteger)arg1).subtract(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return new BigDecimal((BigInteger)arg1).subtract(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg1).subtract((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal((BigInteger)arg1).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof BigDecimal)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return ((BigDecimal)arg1).subtract(new BigDecimal(Utils.toDouble(arg2)));
			else if (arg2 instanceof BigInteger)
				return ((BigDecimal)arg1).subtract(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg1).subtract((BigDecimal)arg2);
		}
		throw new UnsupportedOperationException(Utils.objectType(arg1) + " - " + Utils.objectType(arg2) + " not supported!");
	}
}
