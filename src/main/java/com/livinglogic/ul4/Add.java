/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Add extends Binary
{
	public Add(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public String toString()
	{
		return "add(" + obj1 + ", " + obj2 + ")";
	}

	public int getType()
	{
		return Opcode.OC_ADD;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.add(obj1.evaluate(context), obj2.evaluate(context));
	}
}
