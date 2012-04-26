/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Mod extends Binary
{
	public Mod(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "mod";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.mod(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}
}
