/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class And extends Binary
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
		Object obj2ev = obj2.evaluate(context);
		if (Utils.getBool(obj2ev))
			return obj1.evaluate(context);
		else
			return obj2ev;
	}
}
