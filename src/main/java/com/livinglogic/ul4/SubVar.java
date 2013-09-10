/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class SubVar extends ChangeVar
{
	public SubVar(Location location, int start, int end, LValue lvalue, AST value)
	{
		super(location, start, end, lvalue, value);
	}

	public String getType()
	{
		return "subvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateSub(context, value.decoratedEvaluate(context));
		return null;
	}
}
