/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class PrintX extends AST
{
	protected AST value;

	public PrintX(AST value)
	{
		this.value = value;
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("printx(");
		buffer.append(value.toString(indent));
		buffer.append(")\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "printx";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(Utils.xmlescape(value.evaluate(context)));
		return null;
	}
}
