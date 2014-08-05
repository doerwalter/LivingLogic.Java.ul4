/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class BitAndVarAST extends ChangeVarAST
{
	public BitAndVarAST(Location location, int start, int end, LValue lvalue, AST value)
	{
		super(location, start, end, lvalue, value);
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
