/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class BitOrAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "BitOrAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.bitor";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary bitwise \"or\" expression (e.g. ``x | y``).";
		}

		@Override
		public BitOrAST create(String id)
		{
			return new BitOrAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BitOrAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public BitOrAST(Template Template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(Template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "bitor";
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(EvaluationContext context, int arg1, int arg2)
	{
		return arg1 | arg2;
	}

	public static Object call(EvaluationContext context, long arg1, long arg2)
	{
		return arg1 | arg2;
	}

	public static Object call(EvaluationContext context, BigInteger arg1, BigInteger arg2)
	{
		return arg1.or(arg2);
	}

	public static Object call(EvaluationContext context, Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toInt(arg1), Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call(context, Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof BigInteger)
				return call(context, Utils.toBigInteger(Utils.toInt(arg1)), (BigInteger)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Long || arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof BigInteger)
				return call(context, Utils.toBigInteger(Utils.toLong(arg1)), (BigInteger)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, (BigInteger)arg1, Utils.toBigInteger(Utils.toInt(arg2)));
			else if (arg2 instanceof Long)
				return call(context, (BigInteger)arg1, Utils.toBigInteger(Utils.toLong(arg2)));
			else if (arg2 instanceof BigInteger)
				return call(context, (BigInteger)arg1, (BigInteger)arg2);
		}
		throw new ArgumentTypeMismatchException("{!t} | {!t} not supported", arg1, arg2);
	}
}
