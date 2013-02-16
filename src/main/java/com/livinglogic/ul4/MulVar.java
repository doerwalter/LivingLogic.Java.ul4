/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MulVar extends ChangeVar
{
	public MulVar(Location location, int start, int end, String varname, AST value)
	{
		super(location, start, end, varname, value);
	}

	public String getType()
	{
		return "mulvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(varname, Mul.call(context.get(varname), value.decoratedEvaluate(context)));
		return null;
	}
}
