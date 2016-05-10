/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class BitOrVarAST extends ChangeVarAST
{
	public BitOrVarAST(Tag tag, Slice pos, LValue lvalue, AST value)
	{
		super(tag, pos, lvalue, value);
	}

	public String getType()
	{
		return "bitorvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateBitOr(context, value.decoratedEvaluate(context));
		return null;
	}
}
