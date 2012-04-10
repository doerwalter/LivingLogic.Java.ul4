/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class StoreVar extends ChangeVar
{
	public StoreVar(String varname, AST value)
	{
		super(varname, value);
	}

	public int getType()
	{
		return Opcode.OC_STOREVAR;
	}

	public Object evaluate(EvaluationContext context)
	{
		context.put(varname, value.evaluate(context));
		return null;
	}
}
