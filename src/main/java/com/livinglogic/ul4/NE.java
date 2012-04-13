/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class NE extends Binary
{
	public NE(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public String toString()
	{
		return "ne(" + obj1 + ", " + obj2 + ")";
	}

	public int getType()
	{
		return Opcode.OC_NE;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.ne(obj1.evaluate(context), obj2.evaluate(context));
	}
}
