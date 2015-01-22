/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class BitNotAST extends UnaryAST
{
	public BitNotAST(Tag tag, int start, int end, CodeAST obj)
	{
		super(tag, start, end, obj);
	}

	public String getType()
	{
		return "bitnot";
	}

	public static CodeAST make(Tag tag, int start, int end, CodeAST obj)
	{
		if (obj instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj).value);
			if (!(result instanceof Undefined))
				return new ConstAST(tag, start, end, result);
		}
		return new BitNotAST(tag, start, end, obj);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj.decoratedEvaluate(context));
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Boolean)
			return ~Utils.toInt(obj);
		else if (obj instanceof Long)
			return ~Utils.toLong(obj);
		else if (obj instanceof BigInteger)
			return ((BigInteger)obj).not();
		throw new ArgumentTypeMismatchException("~{}", obj);
	}
}
