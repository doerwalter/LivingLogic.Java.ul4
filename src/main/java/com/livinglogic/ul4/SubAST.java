/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class SubAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "SubAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.sub";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the binary subtraction expression (e.g. ``x - y``).";
		}

		@Override
		public SubAST create(String id)
		{
			return new SubAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof SubAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public SubAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "sub";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(EvaluationContext context, int arg1, int arg2)
	{
		int result = arg1 - arg2;
		if ((arg1 >= 0) == (arg2 >= 0)) // arguments have same sign, so there can be no overflow
			return result;
		else if ((arg1 >= 0) == (result >= 0)) // result didn't change sign, so there was no overflow
			return result;
		else // we had an overflow => promote to BigInteger
			return Utils.toBigInteger(arg1).add(Utils.toBigInteger(arg2).negate());
	}

	public static Object call(EvaluationContext context, long arg1, long arg2)
	{
		long result = arg1 - arg2;
		if ((arg1 >= 0) == (arg2 >= 0)) // arguments have same sign, so there can be no overflow
			return result;
		else if ((arg1 >= 0) == (result >= 0)) // result didn't change sign, so there was no overflow
			return result;
		else // we had an overflow => promote to BigInteger
			return Utils.toBigInteger(arg1).add(Utils.toBigInteger(arg2).negate());
	}

	public static Object call(EvaluationContext context, float arg1, float arg2)
	{
		return arg1 - arg2;
	}

	public static Object call(EvaluationContext context, double arg1, double arg2)
	{
		return arg1 - arg2;
	}

	public static TimeDelta call(EvaluationContext context, Date arg1, Date arg2)
	{
		long diff = arg1.getTime() - arg2.getTime();
		long seconds = diff/1000;
		int milliseconds = (int)(diff % 1000);
		return new TimeDelta(0, seconds, 1000 * milliseconds);
	}

	public static TimeDelta call(EvaluationContext context, Date arg1, LocalDateTime arg2)
	{
		return call(context, arg1.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), arg2);
	}

	public static TimeDelta call(EvaluationContext context, LocalDate arg1, LocalDate arg2)
	{
		long days = ChronoUnit.DAYS.between(arg2, arg1);
		return new TimeDelta(days, 0, 0);
	}

	public static TimeDelta call(EvaluationContext context, LocalDateTime arg1, Date arg2)
	{
		return call(context, arg1, arg2.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}

	public static TimeDelta call(EvaluationContext context, LocalDateTime arg1, LocalDateTime arg2)
	{
		long days = ChronoUnit.DAYS.between(arg2, arg1);
		arg2 = arg2.plusDays(days);
		long hours = ChronoUnit.HOURS.between(arg2, arg1);
		arg2 = arg2.plusHours(hours);
		long minutes = ChronoUnit.MINUTES.between(arg2, arg1);
		arg2 = arg2.plusMinutes(minutes);
		long seconds = ChronoUnit.SECONDS.between(arg2, arg1);
		arg2 = arg2.plusSeconds(seconds);
		long micros = ChronoUnit.MICROS.between(arg2, arg1);
		return new TimeDelta(days, 60 * (60 * hours + minutes) + seconds, micros);
	}

	public static TimeDelta call(EvaluationContext context, TimeDelta arg1, TimeDelta arg2)
	{
		return arg1.subtract(arg2);
	}

	public static Date call(EvaluationContext context, Date arg1, TimeDelta arg2)
	{
		return arg2.subtractFrom(arg1);
	}

	public static LocalDate call(EvaluationContext context, LocalDate arg1, TimeDelta arg2)
	{
		return arg2.subtractFrom(arg1);
	}

	public static LocalDateTime call(EvaluationContext context, LocalDateTime arg1, TimeDelta arg2)
	{
		return arg2.subtractFrom(arg1);
	}

	public static MonthDelta call(EvaluationContext context, MonthDelta arg1, MonthDelta arg2)
	{
		return arg1.subtract(arg2);
	}

	public static Date call(EvaluationContext context, Date arg1, MonthDelta arg2)
	{
		return arg2.subtractFrom(arg1);
	}

	public static LocalDate call(EvaluationContext context, LocalDate arg1, MonthDelta arg2)
	{
		return arg2.subtractFrom(arg1);
	}

	public static LocalDateTime call(EvaluationContext context, LocalDateTime arg1, MonthDelta arg2)
	{
		return arg2.subtractFrom(arg1);
	}

	public static Object call(EvaluationContext context, Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toInt(arg1), Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call(context, Utils.toLong(arg1), ((Long)arg2).longValue());
			else if (arg2 instanceof Float)
				return call(context, Utils.toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof BigInteger)
				return Utils.toBigInteger(Utils.toInt(arg1)).subtract((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(Utils.toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof Float)
				return call(context, Utils.toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof BigInteger)
				return Utils.toBigInteger(Utils.toLong(arg1)).subtract((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(Utils.toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return call(context, Utils.toFloat(arg1), Utils.toFloat(arg2));
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(Utils.toDouble(arg1)).subtract(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(Utils.toDouble(arg1)).subtract((BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), Utils.toDouble(arg2));
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
				return call(context, (Date)arg1, (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call(context, (Date)arg1, (MonthDelta)arg2);
			else if (arg2 instanceof Date)
				return call(context, (Date)arg1, (Date)arg2);
			else if (arg2 instanceof LocalDateTime)
				return call(context, (Date)arg1, (LocalDateTime)arg2);
		}
		else if (arg1 instanceof LocalDate)
		{
			if (arg2 instanceof TimeDelta)
				return call(context, (LocalDate)arg1, (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call(context, (LocalDate)arg1, (MonthDelta)arg2);
			else if (arg2 instanceof LocalDate)
				return call(context, (LocalDate)arg1, (LocalDate)arg2);
		}
		else if (arg1 instanceof LocalDateTime)
		{
			if (arg2 instanceof TimeDelta)
				return call(context, (LocalDateTime)arg1, (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call(context, (LocalDateTime)arg1, (MonthDelta)arg2);
			else if (arg2 instanceof Date)
				return call(context, (LocalDateTime)arg1, (Date)arg2);
			else if (arg2 instanceof LocalDateTime)
				return call(context, (LocalDateTime)arg1, (LocalDateTime)arg2);
		}
		else if (arg1 instanceof TimeDelta)
		{
			if (arg2 instanceof TimeDelta)
				return call(context, (TimeDelta)arg1, (TimeDelta)arg2);
		}
		else if (arg1 instanceof MonthDelta)
		{
			if (arg2 instanceof MonthDelta)
				return call(context, (MonthDelta)arg1, (MonthDelta)arg2);
		}
		throw new ArgumentTypeMismatchException("{!t} - {!t} not supported", arg1, arg2);
	}
}
