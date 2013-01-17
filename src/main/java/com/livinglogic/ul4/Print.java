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

	public String toString(InterpretedTemplate template, int indent)
	{
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("print(");
		buffer.append(obj.toString(template, indent));
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
