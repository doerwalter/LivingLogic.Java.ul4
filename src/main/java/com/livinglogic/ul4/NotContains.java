/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class NotContains extends Binary
{
	public NotContains(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "notcontains";
	}

	public static AST make(Location location, AST obj1, AST obj2)
	{
		if (obj1 instanceof Const && obj2 instanceof Const)
			return new Const(location, call(((Const)obj1).value, ((Const)obj2).value));
		return new NotContains(location, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(Object obj, Object container)
	{
		return !Contains.call(obj, container);
	}
}
