/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class SubVar extends ChangeVar
{
	public SubVar(String varname, AST value)
	{
		super(varname, value);
	}

	public int getType()
	{
		return Opcode.OC_SUBVAR;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(varname, Utils.sub(context.get(varname), value.evaluate(context)));
		return null;
	}
}
