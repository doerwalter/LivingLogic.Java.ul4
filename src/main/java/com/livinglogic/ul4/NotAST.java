/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class NotAST extends UnaryAST
{
	public NotAST(InterpretedTemplate template, Slice pos, CodeAST obj)
	{
		super(template, pos, obj);
	}

	public String getType()
	{
		return "not";
	}

	public static CodeAST make(InterpretedTemplate template, Slice pos, CodeAST obj)
	{
		if (obj instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj).value);
			if (!(result instanceof Undefined))
				return new ConstAST(template, pos, result);
		}
		return new NotAST(template, pos, obj);
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
