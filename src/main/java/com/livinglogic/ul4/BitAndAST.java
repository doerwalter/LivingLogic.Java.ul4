/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class BitAndAST extends BinaryAST
{
	public BitAndAST(Tag tag, int start, int end, AST obj1, AST obj2)
	{
		super(tag, start, end, obj1, obj2);
	}

	public String getType()
	{
		return "bitand";
	}

	public static AST make(Tag tag, int start, int end, AST obj1, AST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			if (!(result instanceof Undefined))
				return new ConstAST(tag, start, end, result);
		}
		return new BitAndAST(tag, start, end, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(int arg1, int arg2)
	{
		return arg1 & arg2;
	}

	public static Object call(long arg1, long arg2)
	{
		return arg1 & arg2;
	}

	public static Object call(BigInteger arg1, BigInteger arg2)
	{
		return arg1.and(arg2);
	}

	public static Object call(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(Utils.toInt(arg1), Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call(Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof BigInteger)
				return call(Utils.toBigInteger(Utils.toInt(arg1)), (BigInteger)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Long || arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof BigInteger)
				return call(Utils.toBigInteger(Utils.toLong(arg1)), (BigInteger)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call((BigInteger)arg1, Utils.toBigInteger(Utils.toInt(arg2)));
			else if (arg2 instanceof Long)
				return call((BigInteger)arg1, Utils.toBigInteger(Utils.toLong(arg2)));
			else if (arg2 instanceof BigInteger)
				return call((BigInteger)arg1, (BigInteger)arg2);
		}
		throw new ArgumentTypeMismatchException("{} & {}", arg1, arg2);
	}
}
