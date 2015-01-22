/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class BitXOrVarAST extends ChangeVarAST
{
	public BitXOrVarAST(Tag tag, int start, int end, LValue lvalue, AST value)
	{
		super(tag, start, end, lvalue, value);
	}

	public String getType()
	{
		return "bitxorvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateBitXOr(context, value.decoratedEvaluate(context));
		return null;
	}
}
