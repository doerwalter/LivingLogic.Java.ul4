/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Mod extends Binary
{
	public Mod(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public String toString()
	{
		return "mod(" + obj1 + ", " + obj2 + ")";
	}

	public int getType()
	{
		return Opcode.OC_MOD;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.mod(obj1.evaluate(context), obj2.evaluate(context));
	}
}