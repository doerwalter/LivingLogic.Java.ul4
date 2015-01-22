/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ShiftRightVarAST extends ChangeVarAST
{
	public ShiftRightVarAST(Tag tag, int start, int end, LValue lvalue, AST value)
	{
		super(tag, start, end, lvalue, value);
	}

	public String getType()
	{
		return "shiftrightvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateShiftRight(context, value.decoratedEvaluate(context));
		return null;
	}
}
