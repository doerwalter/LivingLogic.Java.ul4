/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class BitNotAST extends UnaryAST
{
	public BitNotAST(InterpretedTemplate template, Slice pos, CodeAST obj)
	{
		super(template, pos, obj);
	}

	public String getType()
	{
		return "bitnot";
	}

	public static CodeAST make(InterpretedTemplate template, Slice pos, CodeAST obj)
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
				// fall through to create a real {@code BitNotAST} object
			}
		}
		return new BitNotAST(template, pos, obj);
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
		throw new ArgumentTypeMismatchException("~{!t} not supported", obj);
	}
}
