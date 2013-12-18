/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class ContinueAST extends AST
{
	public ContinueAST(Location location, int start, int end)
	{
		super(location, start, end);
	}

	public String getType()
	{
		return "continue";
	}

	public Object evaluate(EvaluationContext context)
	{
		throw new ContinueException();
	}

	public String toString(int indent)
	{
		return "continue";
	}
}
