/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class Not extends Unary
{
	public Not(Location location, AST obj)
	{
		super(location, obj);
	}

	public String getType()
	{
		return "not";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return !call(obj.decoratedEvaluate(context));
	}

	public static boolean call(Object obj)
	{
		return !FunctionBool.call(obj);
	}
}
