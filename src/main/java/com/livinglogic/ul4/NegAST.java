/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NegAST extends UnaryAST
{
	protected static class Type extends UnaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "NegAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.neg";
		}

		@Override
		public String getDoc()
		{
			return "Unary negation (i.e. `-x`).";
		}

		@Override
		public NegAST create(String id)
		{
			return new NegAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof NegAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public NegAST(Template template, Slice pos, CodeAST obj)
	{
		super(template, pos, obj);
	}

	public String getType()
	{
		return "neg";
	}

	public static CodeAST make(Template template, Slice pos, CodeAST obj)
	{
		if (obj instanceof ConstAST)
		{
			try
			{
				Object result = call(((ConstAST)obj).value);
				if (!(result instanceof Undefined))
					return new ConstAST(template, pos, result);
			}
			catch (Exception ex)
			{
				// fall through to create a real {@code NegAST} object
			}
		}
		return new NegAST(template, pos, obj);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj.decoratedEvaluate(context));
	}

	public static TimeDelta call(TimeDelta arg)
	{
		return arg.negate();
	}

	public static MonthDelta call(MonthDelta arg)
	{
		return arg.negate();
	}

	public static Object call(Object arg)
	{
		if (arg instanceof Integer)
		{
			int value = ((Integer)arg).intValue();
			if (value == -0x80000000) // Prevent overflow by switching to long
				return 0x80000000L;
			else
				return -value;
		}
		else if (arg instanceof Long)
		{
			long value = ((Long)arg).longValue();
			if (value == -0x8000000000000000L) // Prevent overflow by switching to BigInteger
				return new BigInteger("8000000000000000", 16);
			else
				return -value;
		}
		else if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? -1 : 0;
		else if (arg instanceof Short || arg instanceof Byte || arg instanceof Boolean)
			return -((Number)arg).intValue();
		else if (arg instanceof Float)
			return -((Float)arg).floatValue();
		else if (arg instanceof Double)
			return -((Double)arg).doubleValue();
		else if (arg instanceof BigInteger)
			return ((BigInteger)arg).negate();
		else if (arg instanceof BigDecimal)
			return ((BigDecimal)arg).negate();
		else if (arg instanceof TimeDelta)
			return call((TimeDelta)arg);
		else if (arg instanceof MonthDelta)
			return call((MonthDelta)arg);
		throw new ArgumentTypeMismatchException("-{!t}", arg);
	}
}
