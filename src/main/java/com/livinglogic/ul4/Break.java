/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class Break extends AST
{
	public Break(Location location, int start, int end)
	{
		super(location, start, end);
	}

	public String getType()
	{
		return "break";
	}

	public Object evaluate(EvaluationContext context)
	{
		throw new BreakException();
	}

	public String toString(int indent)
	{
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("break\n");
		return buffer.toString();
	}
}
