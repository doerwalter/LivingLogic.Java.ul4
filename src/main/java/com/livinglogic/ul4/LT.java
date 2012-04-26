/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class LT extends Binary
{
	public LT(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "lt";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.lt(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}
}
