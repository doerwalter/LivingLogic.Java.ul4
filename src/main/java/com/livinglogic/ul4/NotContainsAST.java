/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class NotContainsAST extends BinaryAST
{
	public NotContainsAST(Tag tag, int start, int end, CodeAST obj1, CodeAST obj2)
	{
		super(tag, start, end, obj1, obj2);
	}

	public String getType()
	{
		return "notcontains";
	}

	public static CodeAST make(Tag tag, int start, int end, CodeAST obj1, CodeAST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			if (!(result instanceof Undefined))
				return new ConstAST(tag, start, end, result);
		}
		return new NotContainsAST(tag, start, end, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(Object obj, Object container)
	{
		return !ContainsAST.call(obj, container);
	}
}
