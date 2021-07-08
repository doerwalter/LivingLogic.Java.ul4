/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class BitNotAST extends UnaryAST
{
	protected static class Type extends UnaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "BitNotAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.bitnot";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a bitwise unary \"not\" expression that returns its operand\nwith its bits inverted (e.g. ``~x``).";
		}

		@Override
		public BitNotAST create(String id)
		{
			return new BitNotAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BitNotAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public BitNotAST(Template template, Slice pos, CodeAST obj)
	{
		super(template, pos, obj);
	}

	public String getType()
	{
		return "bitnot";
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj.decoratedEvaluate(context));
	}

	public static Object call(EvaluationContext context, Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Boolean)
			return ~Utils.toInt(obj);
		else if (obj instanceof Long)
			return ~Utils.toLong(obj);
		else if (obj instanceof BigInteger)
			return ((BigInteger)obj).not();
		throw new ArgumentTypeMismatchException("~{!t} not supported", obj);
	}
}
