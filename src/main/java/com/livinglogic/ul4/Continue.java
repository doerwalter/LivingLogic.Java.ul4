/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class Continue extends LocationAST
{
	public Continue(Location location)
	{
		super(location);
	}

	public String getType()
	{
		return "continue";
	}

	public Object evaluate(EvaluationContext context)
	{
		throw new ContinueException();
	}

	public String toString(int indent)
	{
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("continue\n");
		return buffer.toString();
	}
}
