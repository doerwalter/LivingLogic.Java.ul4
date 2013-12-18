/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class GEAST extends BinaryAST
{
	public GEAST(Location location, int start, int end, AST obj1, AST obj2)
	{
		super(location, start, end, obj1, obj2);
	}

	public String getType()
	{
		return "ge";
	}

	public static AST make(Location location, int start, int end, AST obj1, AST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			if (!(result instanceof Undefined))
				return new ConstAST(location, start, end, result);
		}
		return new GEAST(location, start, end, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return Utils.cmp(obj1, obj2, ">=") >= 0;
		if ((null == obj1) != (null == obj2))
			throw new ArgumentTypeMismatchException("{} >= {}", obj1, obj2);
		return true;
	}
}