/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class TrueDiv extends Binary
{
	public TrueDiv(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "truediv";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.truediv(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}
}
