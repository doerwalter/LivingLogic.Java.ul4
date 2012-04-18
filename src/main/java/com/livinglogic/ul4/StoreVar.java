/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class StoreVar extends ChangeVar
{
	public StoreVar(String varname, AST value)
	{
		super(varname, value);
	}

	public String getType()
	{
		return "storevar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(varname, value.evaluate(context));
		return null;
	}
}
