/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * {@code ReturnAST} is an unary AST node that can only be used inside functions
 * and that returns an expression from that function.
 */
public class ReturnAST extends UnaryAST
{
	public ReturnAST(InterpretedTemplate template, Slice pos, CodeAST obj)
	{
		super(template, pos, obj);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("return ");
		super.toString(formatter);
	}

	public String getType()
	{
		return "return";
	}

	public Object evaluate(EvaluationContext context)
	{
		throw new ReturnException(obj.decoratedEvaluate(context));
	}
}
