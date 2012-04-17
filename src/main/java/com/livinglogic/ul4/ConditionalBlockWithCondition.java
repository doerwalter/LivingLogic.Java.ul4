/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

abstract class ConditionalBlockWithCondition extends ConditionalBlock
{
	protected AST condition;

	public ConditionalBlockWithCondition(AST condition)
	{
		super();
		this.condition = condition;
	}

	public boolean hasToBeExecuted(EvaluationContext context) throws IOException
	{
		return Utils.getBool(condition.evaluate(context));
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append(name());
		buffer.append(" (");
		buffer.append(condition.toString(indent));
		buffer.append(")\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		for (AST item : content)
			buffer.append(item.toString(indent+1));
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}
}
