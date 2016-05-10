/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ShiftRightVarAST extends ChangeVarAST
{
	public ShiftRightVarAST(Tag tag, Slice pos, LValue lvalue, AST value)
	{
		super(tag, pos, lvalue, value);
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
