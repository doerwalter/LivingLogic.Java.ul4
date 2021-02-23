/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FloorDivAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getModuleName()
		{
			return "ul4";
		}

		@Override
		public String getNameUL4()
		{
			return "FloorDivAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.floordiv";
		}

		@Override
		public String getDoc()
		{
			return "A \"floor division\" expression (x // y).";
		}

		@Override
		public FloorDivAST create(String id)
		{
			return new FloorDivAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof FloorDivAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public FloorDivAST(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(template, pos, obj1, obj2);
	}

	public String getType()
	{
		return "floordiv";
	}

	public static CodeAST make(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			try
			{
				Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
				if (!(result instanceof Undefined))
					return new ConstAST(template, pos, result);
			}
			catch (Exception ex)
			{
				// fall through to create a real {@code FloorDivAST} object
			}
		}
		return new FloorDivAST(template, pos, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(Object arg1, Object arg2)
	{
		// integer division in UL4 is defined is rounding towards -infinity (as Python does)
		// since Java rounds towards 0, the following code compensates for that
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
			{
				int int1 = Utils.toInt(arg1);
				int int2 = Utils.toInt(arg2);
				if (int1 < 0)
				{
					if (int2 < 0)
						return  int1 / int2;
					else
						return  (int1 - int2 + 1) / int2;
				}
				else
				{
					if (int2 < 0)
						return  (int1 - int2 - 1) / int2;
					else
						return  int1 / int2;
				}
			}
			else if (arg2 instanceof Long)
			{
				int int1 = Utils.toInt(arg1);
				long long2 = Utils.toLong(arg2);
				if (int1 < 0)
				{
					if (long2 < 0)
						return  int1 / long2;
					else
						return  (int1 - long2 + 1) / long2;
				}
				else
				{
					if (long2 < 0)
						return  (int1 - long2 - 1) / long2;
					else
						return  int1 / long2;
				}
			}
			else if (arg2 instanceof Float)
			{
				Utils.checkZeroDivisorFloat((Number)arg2);
				return Math.floor(Utils.toInt(arg1) / Utils.toFloat(arg2));
			}
			else if (arg2 instanceof Double)
			{
				Utils.checkZeroDivisorFloat((Number)arg2);
				return Math.floor(Utils.toInt(arg1) / Utils.toDouble(arg2));
			}
			else if (arg2 instanceof BigInteger)
				return Utils.toBigInteger(Utils.toInt(arg1)).divide((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
			{
				Utils.checkZeroDivisorBigDecimal((BigDecimal)arg2);
				return new BigDecimal(Utils.toDouble(arg1)).divideToIntegralValue((BigDecimal)arg2);
			}
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return Utils.toLong(arg1) / Utils.toLong(arg2);
			else if (arg2 instanceof Float)
			{
				Utils.checkZeroDivisorFloat((Number)arg2);
				return Math.floor(Utils.toLong(arg1) / Utils.toFloat(arg2));
			}
			else if (arg2 instanceof Double)
			{
				Utils.checkZeroDivisorFloat((Number)arg2);
				return Math.floor(Utils.toLong(arg1) / Utils.toDouble(arg2));
			}
			else if (arg2 instanceof BigInteger)
				return Utils.toBigInteger(Utils.toLong(arg1)).divide((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(Utils.toDouble(arg1)).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short)
			{
				Utils.checkZeroDivisorInteger((Number)arg2);
				return Math.floor(Utils.toFloat(arg1) / Utils.toFloat(arg2));
			}
			else if (arg2 instanceof Boolean)
			{
				Utils.checkZeroDivisorBoolean((Boolean)arg2);
				return Math.floor(Utils.toFloat(arg1) / Utils.toFloat(arg2));
			}
			else if (arg2 instanceof Float)
			{
				Utils.checkZeroDivisorFloat((Number)arg2);
				return Math.floor(Utils.toFloat(arg1) / Utils.toFloat(arg2));
			}
			else if (arg2 instanceof Double)
			{
				Utils.checkZeroDivisorFloat((Number)arg2);
				return Math.floor(Utils.toDouble(arg1) / (((Double)arg2).doubleValue()));
			}
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(Utils.toDouble(arg1)).divideToIntegralValue(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(Utils.toDouble(arg1)).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short)
			{
				Utils.checkZeroDivisorInteger((Number)arg2);
				return Math.floor(value1 / Utils.toDouble(arg2));
			}
			else if (arg2 instanceof Boolean)
			{
				Utils.checkZeroDivisorBoolean((Boolean)arg2);
				return Math.floor(value1 / Utils.toDouble(arg2));
			}
			else if (arg2 instanceof Float || arg2 instanceof Double)
			{
				Utils.checkZeroDivisorFloat((Number)arg2);
				return Math.floor(value1 / Utils.toDouble(arg2));
			}
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(value1).divideToIntegralValue(new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(value1).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.divide(Utils.toBigInteger(Utils.toInt(arg2)));
			else if (arg2 instanceof Long)
				return value1.divide(Utils.toBigInteger(Utils.toLong(arg2)));
			else if (arg2 instanceof Float)
				return new BigDecimal(value1).divideToIntegralValue(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return new BigDecimal(value1).divideToIntegralValue(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return value1.divide((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(value1).divideToIntegralValue((BigDecimal)arg2);
		}
		else if (arg1 instanceof BigDecimal)
		{
			BigDecimal value1 = (BigDecimal)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return value1.divideToIntegralValue(Utils.toBigDecimal(Utils.toInt(arg2))).toBigInteger();
			else if (arg2 instanceof Long)
				return value1.divideToIntegralValue(Utils.toBigDecimal(Utils.toLong(arg2))).toBigInteger();
			else if (arg2 instanceof Float)
				return value1.divideToIntegralValue(new BigDecimal(((Float)arg2).doubleValue())).toBigInteger();
			else if (arg2 instanceof Double)
				return value1.divideToIntegralValue(new BigDecimal(((Double)arg2).doubleValue())).toBigInteger();
			else if (arg2 instanceof BigInteger)
				return value1.divideToIntegralValue(new BigDecimal((BigInteger)arg2)).toBigInteger();
			else if (arg2 instanceof BigDecimal)
				return value1.divideToIntegralValue((BigDecimal)arg2).toBigInteger();
		}
		else if (arg1 instanceof TimeDelta)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return ((TimeDelta)arg1).floordiv(Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return ((TimeDelta)arg1).floordiv(Utils.toLong(arg2));
			else if (arg2 instanceof TimeDelta)
				return ((TimeDelta)arg1).floordiv((TimeDelta)arg2);
			else if (arg2 instanceof BigInteger)
				return ((TimeDelta)arg1).floordiv((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return ((TimeDelta)arg1).floordiv((BigDecimal)arg2);
		}
		else if (arg1 instanceof MonthDelta)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return ((MonthDelta)arg1).floordiv(Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return ((MonthDelta)arg1).floordiv(Utils.toLong(arg2));
			else if (arg2 instanceof MonthDelta)
				return ((MonthDelta)arg1).floordiv((MonthDelta)arg2);
		}
		throw new ArgumentTypeMismatchException("{!t} // {!t} not supported", arg1, arg2);
	}
}
