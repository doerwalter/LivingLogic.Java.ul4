/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class NotAST extends UnaryAST
{
	public NotAST(Location location, int start, int end, AST obj)
	{
		super(location, start, end, obj);
	}

	public String getType()
	{
		return "not";
	}

	public static AST make(Location location, int start, int end, AST obj)
	{
		if (obj instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj).value);
			if (!(result instanceof Undefined))
				return new ConstAST(location, start, end, result);
		}
		return new NotAST(location, start, end, obj);
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
