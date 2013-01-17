/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

/**
 * {@code PrintX} is an unary Tag node that writes a string version of its
 * operand to the output stream and replaces the characters {@code <}, {@code >},
 * {@code &}, {@code '} and {@code "} with the appropriate XML character
 * entities.
 */
public class PrintX extends UnaryTag
{
	public PrintX(Location location, AST obj)
	{
		super(location, obj);
	}

	public String toString(InterpretedTemplate template, int indent)
	{
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("printx(");
		buffer.append(obj.toString(template, indent));
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
