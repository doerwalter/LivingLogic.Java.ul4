/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class TrueDivVar extends ChangeVar
{
	public TrueDivVar(String varname, AST value)
	{
		super(varname, value);
	}

	public int getType()
	{
		return Opcode.OC_TRUEDIVVAR;
	}

	public Object evaluate(EvaluationContext context)
	{
		context.put(varname, Utils.truediv(context.get(varname), value.evaluate(context)));
		return null;
	}
}
