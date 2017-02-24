/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ShiftLeftVarAST extends ChangeVarAST
{
	public ShiftLeftVarAST(Tag tag, Slice pos, LValue lvalue, AST value)
	{
		super(tag, pos, lvalue, value);
	}

	public String getType()
	{
		return "shiftleftvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateShiftLeft(context, value.decoratedEvaluate(context));
		return null;
	}
}
