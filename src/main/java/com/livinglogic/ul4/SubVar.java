/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class SubVar extends ChangeVar
{
	public SubVar(Location location, String varname, AST value)
	{
		super(location, varname, value);
	}

	public String getType()
	{
		return "subvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(varname, Sub.call(context.get(varname), value.decoratedEvaluate(context)));
		return null;
	}
}
