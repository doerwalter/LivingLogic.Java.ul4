/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "AddAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.add";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary addition expression (e.g. ``x + y``).";
		}

		@Override
		public AddAST create(String id)
		{
			return new AddAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof AddAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public AddAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "add";
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(EvaluationContext context, int arg1, int arg2)
	{
		int result = arg1 + arg2;
		if ((arg1 >= 0) != (arg2 >= 0)) // arguments have different sign, so there can be no overflow
			return result;
		else if ((arg1 >= 0) == (result >= 0)) // result didn't change sign, so there was no overflow
			return result;
		else // we had an overflow => promote to BigInteger
			return Utils.toBigInteger(arg1).add(Utils.toBigInteger(arg2));
	}

	public static Object call(EvaluationContext context, long arg1, long arg2)
	{
		long result = arg1 + arg2;
		if ((arg1 >= 0) != (arg2 >= 0)) // arguments have different sign, so there can be no overflow
			return result;
		else if ((arg1 >= 0) == (result >= 0)) // result didn't change sign, so there was no overflow
			return result;
		else // we had an overflow => promote to BigInteger
			return Utils.toBigInteger(arg1).add(Utils.toBigInteger(arg2));
	}

	public static Object call(EvaluationContext context, float arg1, float arg2)
	{
		return arg1 + arg2;
	}

	public static Object call(EvaluationContext context, double arg1, double arg2)
	{
		return arg1 + arg2;
	}

	public static Object call(EvaluationContext context, String arg1, String arg2)
	{
		return arg1 + arg2;
	}

	public static Object call(EvaluationContext context, List arg1, List arg2)
	{
		ArrayList result = new ArrayList(arg1.size() + arg2.size());

		result.addAll(arg1);
		result.addAll(arg2);

		return result;
	}

	public static TimeDelta call(EvaluationContext context, TimeDelta arg1, TimeDelta arg2)
	{
		return arg1.add(arg2);
	}

	public static Date call(EvaluationContext context, Date arg1, TimeDelta arg2)
	{
		return arg2.addTo(arg1);
	}

	public static LocalDate call(EvaluationContext context, LocalDate arg1, TimeDelta arg2)
	{
		return arg2.addTo(arg1);
	}

	public static LocalDateTime call(EvaluationContext context, LocalDateTime arg1, TimeDelta arg2)
	{
		return arg2.addTo(arg1);
	}

	public static Date call(EvaluationContext context, TimeDelta arg1, Date arg2)
	{
		return arg1.addTo(arg2);
	}

	public static LocalDate call(EvaluationContext context, TimeDelta arg1, LocalDate arg2)
	{
		return arg1.addTo(arg2);
	}

	public static LocalDateTime call(EvaluationContext context, TimeDelta arg1, LocalDateTime arg2)
	{
		return arg1.addTo(arg2);
	}

	public static MonthDelta call(EvaluationContext context, MonthDelta arg1, MonthDelta arg2)
	{
		return arg1.add(arg2);
	}

	public static Date call(EvaluationContext context, Date arg1, MonthDelta arg2)
	{
		return arg2.addTo(arg1);
	}

	public static LocalDate call(EvaluationContext context, LocalDate arg1, MonthDelta arg2)
	{
		return arg2.addTo(arg1);
	}

	public static LocalDateTime call(EvaluationContext context, LocalDateTime arg1, MonthDelta arg2)
	{
		return arg2.addTo(arg1);
	}

	public static Date call(EvaluationContext context, MonthDelta arg1, Date arg2)
	{
		return arg1.addTo(arg2);
	}

	public static LocalDate call(EvaluationContext context, MonthDelta arg1, LocalDate arg2)
	{
		return arg1.addTo(arg2);
	}

	public static LocalDateTime call(EvaluationContext context, MonthDelta arg1, LocalDateTime arg2)
	{
		return arg1.addTo(arg2);
	}

	public static Object call(EvaluationContext context, Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toInt(arg1), Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call(context, Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof Float)
				return call(context, Utils.toFloat(arg1), Utils.toFloat(arg2));
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), Utils.toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).add(Utils.toBigInteger(Utils.toInt(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(Utils.toDouble(arg1)));
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof Float)
				return call(context, Utils.toFloat(arg1), Utils.toFloat(arg2));
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), Utils.toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).add(Utils.toBigInteger(Utils.toLong(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(Utils.toDouble(arg1)));
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return call(context, Utils.toFloat(arg1), Utils.toFloat(arg2));
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).add(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(Utils.toDouble(arg1)));
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return call(context, value1, Utils.toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).add(new BigDecimal(value1));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(value1));
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.add(Utils.toBigInteger(Utils.toInt(arg2)));
			else if (arg2 instanceof Long)
				return value1.add(Utils.toBigInteger(Utils.toLong(arg2)));
			else if (arg2 instanceof Float)
				return new BigDecimal(value1).add(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return new BigDecimal(value1).add(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return value1.add((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).add(new BigDecimal(value1));
		}
		else if (arg1 instanceof BigDecimal)
		{
			BigDecimal value1 = (BigDecimal)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.add(Utils.toBigDecimal(Utils.toInt(arg2)));
			else if (arg2 instanceof Long)
				return value1.add(Utils.toBigDecimal(Utils.toLong(arg2)));
			else if (arg2 instanceof Float)
				return value1.add(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return value1.add(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return value1.add(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return value1.add((BigDecimal)arg2);
		}
		else if (arg1 instanceof Date)
		{
			if (arg2 instanceof TimeDelta)
				return call(context, (Date)arg1, (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call(context, (Date)arg1, (MonthDelta)arg2);
		}
		else if (arg1 instanceof LocalDate)
		{
			if (arg2 instanceof TimeDelta)
				return call(context, (LocalDate)arg1, (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call(context, (LocalDate)arg1, (MonthDelta)arg2);
		}
		else if (arg1 instanceof LocalDateTime)
		{
			if (arg2 instanceof TimeDelta)
				return call(context, (LocalDateTime)arg1, (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call(context, (LocalDateTime)arg1, (MonthDelta)arg2);
		}
		else if (arg1 instanceof TimeDelta)
		{
			if (arg2 instanceof Date)
				return call(context, (Date)arg2, (TimeDelta)arg1);
			else if (arg2 instanceof LocalDate)
				return call(context, (LocalDate)arg2, (TimeDelta)arg1);
			else if (arg2 instanceof LocalDateTime)
				return call(context, (LocalDateTime)arg2, (TimeDelta)arg1);
			else if (arg2 instanceof TimeDelta)
				return call(context, (TimeDelta)arg2, (TimeDelta)arg1);
		}
		else if (arg1 instanceof MonthDelta)
		{
			if (arg2 instanceof Date)
				return call(context, (Date)arg2, (MonthDelta)arg1);
			else if (arg2 instanceof LocalDate)
				return call(context, (LocalDate)arg2, (MonthDelta)arg1);
			else if (arg2 instanceof LocalDateTime)
				return call(context, (LocalDateTime)arg2, (MonthDelta)arg1);
			else if (arg2 instanceof MonthDelta)
				return call(context, (MonthDelta)arg2, (MonthDelta)arg1);
		}
		else if (arg1 instanceof String && arg2 instanceof String)
			return call(context, (String)arg1, (String)arg2);
		else if (arg1 instanceof List && arg2 instanceof List)
			return call(context, (List)arg1, (List)arg2);
		throw new ArgumentTypeMismatchException("{!t} + {!t} not supported", arg1, arg2);
	}
}
