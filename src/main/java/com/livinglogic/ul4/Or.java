/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Or extends Binary
{
	public Or(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public String getType()
	{
		return "or";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object obj1ev = obj1.evaluate(context);
		if (Utils.getBool(obj1ev))
			return obj1ev;
		else
			return obj2.evaluate(context);
	}
}
