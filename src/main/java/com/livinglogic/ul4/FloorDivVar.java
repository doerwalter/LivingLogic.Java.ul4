/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class FloorDivVar extends ChangeVar
{
	public FloorDivVar(String varname, AST value)
	{
		super(varname, value);
	}

	public String name()
	{
		return "floordivvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(varname, Utils.floordiv(context.get(varname), value.evaluate(context)));
		return null;
	}
}
