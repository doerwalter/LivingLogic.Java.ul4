/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FloorDivVarAST extends ChangeVarAST
{
	public FloorDivVarAST(InterpretedTemplate template, Slice pos, LValue lvalue, AST value)
	{
		super(template, pos, lvalue, value);
	}

	public String getType()
	{
		return "floordivvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateFloorDiv(context, value.decoratedEvaluate(context));
		return null;
	}
}
