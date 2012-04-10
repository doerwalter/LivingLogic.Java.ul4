/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class Not extends Unary
{
	public Not(AST obj)
	{
		super(obj);
	}

	public int getType()
	{
		return Opcode.OC_NOT;
	}

	public String toString()
	{
		return "not(" + obj + ")";
	}

	public Object evaluate(EvaluationContext context)
	{
		return !Utils.getBool(obj.evaluate(context));
	}
}
