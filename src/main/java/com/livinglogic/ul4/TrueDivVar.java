/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class TrueDivVar extends ChangeVar
{
	public TrueDivVar(String varname, AST value)
	{
		super(varname, value);
	}

	public String getType()
	{
		return "truedivvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(varname, Utils.truediv(context.get(varname), value.evaluate(context)));
		return null;
	}
}
