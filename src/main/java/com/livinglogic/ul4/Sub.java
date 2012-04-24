/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Sub extends Binary
{
	public Sub(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "sub";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.sub(obj1.evaluate(context), obj2.evaluate(context));
	}
}
