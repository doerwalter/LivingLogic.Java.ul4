/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class GT extends Binary
{
	public GT(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_GT;
	}

	public Object evaluate(EvaluationContext context)
	{
		return Utils.gt(obj1.evaluate(context), obj2.evaluate(context));
	}
}
