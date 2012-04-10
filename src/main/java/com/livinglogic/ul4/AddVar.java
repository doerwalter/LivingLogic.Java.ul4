/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class AddVar extends ChangeVar
{
	public AddVar(String varname, AST value)
	{
		super(varname, value);
	}

	public int getType()
	{
		return Opcode.OC_ADDVAR;
	}

	public Object evaluate(EvaluationContext context)
	{
		context.put(varname, Utils.add(context.get(varname), value.evaluate(context)));
		return null;
	}
}
