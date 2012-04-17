/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class Continue extends AST
{
	public String name()
	{
		return "continue";
	}

	public Object evaluate(EvaluationContext context)
	{
		throw new ContinueException();
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("continue\n");
		return buffer.toString();
	}
}
