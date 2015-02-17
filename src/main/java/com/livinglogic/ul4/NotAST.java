/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class NotAST extends UnaryAST
{
	public NotAST(Tag tag, int start, int end, CodeAST obj)
	{
		super(tag, start, end, obj);
	}

	public String getType()
	{
		return "not";
	}

	public static CodeAST make(Tag tag, int start, int end, CodeAST obj)
	{
		if (obj instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj).value);
			if (!(result instanceof Undefined))
				return new ConstAST(tag, start, end, result);
		}
		return new NotAST(tag, start, end, obj);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj.decoratedEvaluate(context));
	}

	public static boolean call(Object obj)
	{
		return !FunctionBool.call(obj);
	}
}
