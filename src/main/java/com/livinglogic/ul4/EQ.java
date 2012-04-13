/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class EQ extends Binary
{
	public EQ(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public String toString()
	{
		return "eq(" + obj1 + ", " + obj2 + ")";
	}

	public int getType()
	{
		return Opcode.OC_EQ;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.eq(obj1.evaluate(context), obj2.evaluate(context));
	}
}