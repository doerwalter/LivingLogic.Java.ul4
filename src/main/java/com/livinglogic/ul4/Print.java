/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Print extends AST
{
	protected AST value;

	public Print(AST value)
	{
		this.value = value;
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("print(");
		buffer.append(value.toString(indent));
		buffer.append(")\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "print";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(Utils.str(value.evaluate(context)));
		return null;
	}
}
