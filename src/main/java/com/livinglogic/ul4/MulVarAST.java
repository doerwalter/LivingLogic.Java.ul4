/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class MulVarAST extends ChangeVarAST
{
	public MulVarAST(InterpretedTemplate template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
	}

	public String getType()
	{
		return "mulvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateMul(context, value.decoratedEvaluate(context));
		return null;
	}
}
