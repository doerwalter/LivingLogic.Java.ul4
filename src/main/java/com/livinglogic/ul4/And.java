/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class And extends Binary
{
	public And(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "and";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object obj2ev = obj2.decoratedEvaluate(context);
		if (FunctionBool.call(obj2ev))
			return obj1.decoratedEvaluate(context);
		else
			return obj2ev;
	}

	// we can't implement a static call version here, because that would require that we evaluate both sides
}
