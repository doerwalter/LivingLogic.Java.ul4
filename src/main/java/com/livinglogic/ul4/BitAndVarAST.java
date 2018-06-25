/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class BitAndVarAST extends ChangeVarAST
{
	public BitAndVarAST(Tag tag, Slice pos, LValue lvalue, AST value)
	{
		super(tag, pos, lvalue, value);
	}

	public String getType()
	{
		return "bitandvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateBitAnd(context, value.decoratedEvaluate(context));
		return null;
	}
}
