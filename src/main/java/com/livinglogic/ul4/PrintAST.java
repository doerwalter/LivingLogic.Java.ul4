/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * {@code PrintAST} is an unary AST node that writes a string version of its
 * operand to the output stream.
 */
public class PrintAST extends UnaryAST
{
	public PrintAST(Tag tag, Slice pos, CodeAST obj)
	{
		super(tag, pos, obj);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("print ");
		super.toString(formatter);
	}

	public String getType()
	{
		return "print";
	}

	public Object evaluate(EvaluationContext context)
	{
		context.write(FunctionStr.call(obj.decoratedEvaluate(context)));
		return null;
	}
}
