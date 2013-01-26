/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

/**
 * {@code Return} is an unary Tag node that can only be used inside functions
 * and that returns an expression from that function.
 */
public class Return extends UnaryTag
{
	public Return(Location location, AST obj)
	{
		super(location, obj);
	}

	public String toString(InterpretedCode code, int indent)
	{
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("return ");
		buffer.append(obj.toString(code, indent));
		buffer.append("\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "return";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		throw new ReturnException(obj.decoratedEvaluate(context));
	}
}
