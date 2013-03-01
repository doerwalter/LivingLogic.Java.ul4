/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class EQ extends Binary
{
	public EQ(Location location, int start, int end, AST obj1, AST obj2)
	{
		super(location, start, end, obj1, obj2);
	}

	public String getType()
	{
		return "eq";
	}

	public static AST make(Location location, int start, int end, AST obj1, AST obj2)
	{
		if (obj1 instanceof Const && obj2 instanceof Const)
		{
			Object result = call(((Const)obj1).value, ((Const)obj2).value);
			if (!(result instanceof Undefined))
				return new Const(location, start, end, result);
		}
		return new EQ(location, start, end, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return Utils.cmp(obj1, obj2, "==") == 0;
		return (null == obj1) == (null == obj2);
	}
}
