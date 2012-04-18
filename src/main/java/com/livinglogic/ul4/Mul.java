/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Mul extends Binary
{
	public Mul(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public String getType()
	{
		return "mul";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.mul(obj1.evaluate(context), obj2.evaluate(context));
	}
}
