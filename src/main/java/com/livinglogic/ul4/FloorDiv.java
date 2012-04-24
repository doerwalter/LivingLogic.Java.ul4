/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class FloorDiv extends Binary
{
	public FloorDiv(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "floordiv";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.floordiv(obj1.evaluate(context), obj2.evaluate(context));
	}
}
