/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class TrueDivVarAST extends ChangeVarAST
{
	public TrueDivVarAST(Tag tag, int start, int end, LValue lvalue, AST value)
	{
		super(tag, start, end, lvalue, value);
	}

	public String getType()
	{
		return "truedivvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateTrueDiv(context, value.decoratedEvaluate(context));
		return null;
	}
}
