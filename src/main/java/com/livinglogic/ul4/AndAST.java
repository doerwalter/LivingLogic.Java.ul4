/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class AndAST extends BinaryAST
{
	public AndAST(Location location, int start, int end, AST obj1, AST obj2)
	{
		super(location, start, end, obj1, obj2);
	}

	public String getType()
	{
		return "and";
	}

	public static AST make(Location location, int start, int end, AST obj1, AST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			if (!(result instanceof Undefined))
				return new ConstAST(location, start, end, result);
		}
		return new AndAST(location, start, end, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		Object obj1ev = obj1.decoratedEvaluate(context);
		if (!FunctionBool.call(obj1ev))
			return obj1ev;
		else
			return obj2.decoratedEvaluate(context);
	}

	// this static version is only used for constant folding, not in evaluate(), because that would require that we evaluate both sides
	public static Object call(Object arg1, Object arg2)
	{
		return !FunctionBool.call(arg1) ? arg1 : arg2;
	}
}