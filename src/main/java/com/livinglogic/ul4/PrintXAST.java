/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * {@code PrintXAST} is an unary AST node that writes a string version of its
 * operand to the output stream and replaces the characters {@code <}, {@code >},
 * {@code &}, {@code '} and {@code "} with the appropriate XML character
 * entities.
 */
public class PrintXAST extends UnaryAST
{
	public PrintXAST(Tag tag, int start, int end, CodeAST obj)
	{
		super(tag, start, end, obj);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("printx ");
		super.toString(formatter);
	}

	public String getType()
	{
		return "printx";
	}

	public Object evaluate(EvaluationContext context)
	{
		context.write(FunctionXMLEscape.call(obj.decoratedEvaluate(context)));
		return null;
	}
}
