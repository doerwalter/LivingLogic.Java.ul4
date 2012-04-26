/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Neg extends Unary
{
	public Neg(Location location, AST obj)
	{
		super(location, obj);
	}

	public String getType()
	{
		return "neg";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.neg(obj.decoratedEvaluate(context));
	}
}
