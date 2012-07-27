/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class TrueDivVar extends ChangeVar
{
	public TrueDivVar(Location location, String varname, AST value)
	{
		super(location, varname, value);
	}

	public String getType()
	{
		return "truedivvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(varname, TrueDiv.call(context.get(varname), value.decoratedEvaluate(context)));
		return null;
	}
}
