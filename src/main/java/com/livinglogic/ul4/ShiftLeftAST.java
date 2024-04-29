/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class ShiftLeftAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ShiftLeftAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.shiftleft";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a bitwise left shift expression (e.g. ``x << y``).";
		}

		@Override
		public ShiftLeftAST create(String id)
		{
			return new ShiftLeftAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ShiftLeftAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ShiftLeftAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "shiftleft";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(EvaluationContext context, int arg1, int arg2)
	{
		if (arg2 < 0)
			return ShiftRightAST.call(context, arg1, -arg2);
		else if (arg1 != 0 && arg2 >= 32)
			return call(context, Utils.toBigInteger(arg1), arg2);
		int result = arg1 << arg2;
		if (((result >> arg2) != arg1) || (result < 0 && arg1 > 0) || (result > 0 && arg1 < 0))
			return call(context, Utils.toBigInteger(arg1), arg2);
		return result;
	}

	public static Object call(EvaluationContext context, long arg1, int arg2)
	{
		if (arg2 < 0)
			return ShiftRightAST.call(context, arg1, -arg2);
		else if (arg1 != 0 && arg2 >= 64)
			return call(context, Utils.toBigInteger(arg1), arg2);
		long result = arg1 << arg2;
		if (((result >> arg2) != arg1) || (result < 0 && arg1 > 0) || (result > 0 && arg1 < 0))
			return call(context, Utils.toBigInteger(arg1), arg2);
		return result;
	}

	public static Object call(EvaluationContext context, BigInteger arg1, int arg2)
	{
		return arg1.shiftLeft(arg2);
	}

	public static Object call(EvaluationContext context, Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof BigInteger || arg2 instanceof Long || arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toInt(arg1), Utils.toInt(arg2));
		}
		if (arg1 instanceof Long)
		{
			if (arg2 instanceof BigInteger || arg2 instanceof Long || arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toLong(arg1), Utils.toInt(arg2));
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof BigInteger || arg2 instanceof Long || arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, (BigInteger)arg1, Utils.toInt(arg2));
		}
		throw new ArgumentTypeMismatchException("{!t} << {!t} not supported", arg1, arg2);
	}
}
