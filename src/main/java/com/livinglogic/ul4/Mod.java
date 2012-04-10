/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class Mod extends Binary
{
	public Mod(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_MOD;
	}

	public Object evaluate(EvaluationContext context)
	{
		return Utils.mod(obj1.evaluate(context), obj2.evaluate(context));
	}
}
