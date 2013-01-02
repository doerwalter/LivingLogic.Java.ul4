/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class Not extends Unary
{
	public Not(AST obj)
	{
		super(obj);
	}

	public String getType()
	{
		return "not";
	}

	public static AST make(AST obj)
	{
		if (obj instanceof Const)
		{
			Object result = call(((Const)obj).value);
			if (!(result instanceof Undefined))
				return new Const(result);
		}
		return new Not(obj);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return call(obj.decoratedEvaluate(context));
	}

	public static boolean call(Object obj)
	{
		return !FunctionBool.call(obj);
	}
}
