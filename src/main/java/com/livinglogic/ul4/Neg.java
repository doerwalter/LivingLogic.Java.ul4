/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Neg extends Unary
{
	public Neg(AST obj)
	{
		super(obj);
	}

	public int getType()
	{
		return Opcode.OC_NEG;
	}

	public String toString()
	{
		return "neg(" + obj + ")";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.neg(obj.evaluate(context));
	}
}
