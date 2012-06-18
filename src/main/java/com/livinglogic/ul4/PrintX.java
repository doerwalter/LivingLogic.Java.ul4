/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;

class PrintX extends Unary
{
	public PrintX(Location location, AST obj)
	{
		super(location, obj);
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("printx(");
		buffer.append(obj.toString(indent));
		buffer.append(")\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "printx";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(FunctionXMLEscape.call(obj.decoratedEvaluate(context)));
		return null;
	}
}
