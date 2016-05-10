/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class SubAST extends BinaryAST
{
	public SubAST(Tag tag, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(tag, pos, obj1, obj2);
	}

	public String getType()
	{
		return "sub";
	}

	public static CodeAST make(Tag tag, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			if (!(result instanceof Undefined))
				return new ConstAST(tag, pos, result);
		}
		return new SubAST(tag, pos, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
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

	public static TimeDelta call(Date arg1, Date arg2)
	{
		long diff = arg1.getTime() - arg2.getTime();
		long seconds = diff/1000;
		int milliseconds = (int)(diff % 1000);
		return new TimeDelta(0, seconds, 1000 * milliseconds);
	}

	public static TimeDelta call(TimeDelta arg1, TimeDelta arg2)
	{
		return arg1.subtract(arg2);
	}

	public static Date call(Date arg1, TimeDelta arg2)
	{
		return arg2.subtractFrom(arg1);
	}

	public static MonthDelta call(MonthDelta arg1, MonthDelta arg2)
	{
		return arg1.subtract(arg2);
	}

	public static Date call(Date arg1, MonthDelta arg2)
	{
		return arg2.subtractFrom(arg1);
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
		else if (arg1 instanceof Date)
		{
			if (arg2 instanceof TimeDelta)
				return call((Date)arg1, (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call((Date)arg1, (MonthDelta)arg2);
			else if (arg2 instanceof Date)
				return call((Date)arg1, (Date)arg2);
		}
		else if (arg1 instanceof TimeDelta)
		{
			if (arg2 instanceof TimeDelta)
				return call((TimeDelta)arg1, (TimeDelta)arg2);
		}
		else if (arg1 instanceof MonthDelta)
		{
			if (arg2 instanceof MonthDelta)
				return call((MonthDelta)arg1, (MonthDelta)arg2);
		}
		throw new ArgumentTypeMismatchException("{!t} - {!t} not supported", arg1, arg2);
	}
}
