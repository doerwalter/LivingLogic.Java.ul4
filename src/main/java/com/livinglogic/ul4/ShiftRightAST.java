/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class ShiftRightAST extends BinaryAST
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
			return "ShiftRightAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.shiftright";
		}

		@Override
		public String getDoc()
		{
			return "A bit shift right expression (i.e. `x >> y`).";
		}

		@Override
		public ShiftRightAST create(String id)
		{
			return new ShiftRightAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ShiftRightAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ShiftRightAST(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(template, pos, obj1, obj2);
	}

	public String getType()
	{
		return "shiftright";
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
				// fall through to create a real {@code ShiftRightAST} object
			}
		}
		return new ShiftRightAST(template, pos, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(int arg1, int arg2)
	{
		if (arg2 < 0)
			return ShiftLeftAST.call(arg1, -arg2);
		else if (arg2 >= 32)
			return arg1 < 0 ? -1 : 0;
		return arg1 >> arg2;
	}

	public static Object call(long arg1, int arg2)
	{
		if (arg2 < 0)
			return ShiftLeftAST.call(arg1, -arg2);
		else if (arg2 >= 64)
			return arg1 < 0 ? -1 : 0;
		return arg1 >> arg2;
	}

	public static Object call(BigInteger arg1, int arg2)
	{
		return arg1.shiftRight(arg2);
	}

	public static Object call(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof BigInteger || arg2 instanceof Long || arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(Utils.toInt(arg1), Utils.toInt(arg2));
		}
		if (arg1 instanceof Long)
		{
			if (arg2 instanceof BigInteger || arg2 instanceof Long || arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(Utils.toLong(arg1), Utils.toInt(arg2));
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof BigInteger || arg2 instanceof Long || arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call((BigInteger)arg1, Utils.toInt(arg2));
		}
		throw new ArgumentTypeMismatchException("{!t} >> {!t} not supported", arg1, arg2);
	}
}
