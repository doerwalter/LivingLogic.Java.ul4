/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class Not extends Unary
{
	public Not(Location location, int start, int end, AST obj)
	{
		super(location, start, end, obj);
	}

	public String getType()
	{
		return "not";
	}

	public static AST make(Location location, int start, int end, AST obj)
	{
		if (obj instanceof Const)
		{
			Object result = call(((Const)obj).value);
			if (!(result instanceof Undefined))
				return new Const(location, start, end, result);
		}
		return new Not(location, start, end, obj);
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
