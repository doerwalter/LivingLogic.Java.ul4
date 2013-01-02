/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

/**
 * {@code Print} is an unary Tag node that writes a string version of its
 * operand to the output stream.
 */
public class Print extends UnaryTag
{
	public Print(Location location, AST obj)
	{
		super(location, obj);
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("print(");
		buffer.append(obj.toString(indent));
		buffer.append(")\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "print";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(FunctionStr.call(obj.decoratedEvaluate(context)));
		return null;
	}
}
