/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FloorDivVar extends ChangeVar
{
	public FloorDivVar(Location location, int start, int end, String varname, AST value)
	{
		super(location, start, end, varname, value);
	}

	public String getType()
	{
		return "floordivvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		context.put(varname, FloorDiv.call(context.get(varname), value.decoratedEvaluate(context)));
		return null;
	}
}
