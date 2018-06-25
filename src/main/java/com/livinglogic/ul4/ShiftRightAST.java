/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
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
	public ShiftRightAST(Tag tag, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(tag, pos, obj1, obj2);
	}

	public String getType()
	{
		return "shiftright";
	}

	public static CodeAST make(Tag tag, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			if (!(result instanceof Undefined))
				return new ConstAST(tag, pos, result);
		}
		return new ShiftRightAST(tag, pos, obj1, obj2);
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
