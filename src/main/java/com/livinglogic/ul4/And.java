/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class And extends Binary
{
	public And(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public String getType()
	{
		return "and";
	}

	public static AST make(AST obj1, AST obj2)
	{
		if (obj1 instanceof Const && obj2 instanceof Const)
		{
			Object result = call(((Const)obj1).value, ((Const)obj2).value);
			if (!(result instanceof Undefined))
				return new Const(result);
		}
		return new And(obj1, obj2);
	}

	public Object evaluate(EvaluationContext context) throws IOException
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
