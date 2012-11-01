/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class NE extends Binary
{
	public NE(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "ne";
	}

	public static AST make(Location location, AST obj1, AST obj2)
	{
		if (obj1 instanceof Const && obj2 instanceof Const)
			return new Const(location, call(((Const)obj1).value, ((Const)obj2).value));
		return new NE(location, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(Object obj1, Object obj2)
	{
		if (null != obj1 && null != obj2)
			return Utils.cmp(obj1, obj2, "!=") != 0;
		return (null == obj1) != (null == obj2);
	}
}
