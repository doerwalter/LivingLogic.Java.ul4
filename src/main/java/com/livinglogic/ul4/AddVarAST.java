/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class AddVarAST extends ChangeVarAST
{
	public AddVarAST(Location location, int start, int end, LValue lvalue, AST value)
	{
		super(location, start, end, lvalue, value);
	}

	public String getType()
	{
		return "addvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateAdd(context, value.decoratedEvaluate(context));
		return null;
	}
}
