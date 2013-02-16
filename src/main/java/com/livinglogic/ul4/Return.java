/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

/**
 * {@code Return} is an unary AST node that can only be used inside functions
 * and that returns an expression from that function.
 */
public class Return extends Unary
{
	public Return(Location location, int start, int end, AST obj)
	{
		super(location, start, end, obj);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("return ");
		super.toString(formatter);
	}

	public String getType()
	{
		return "return";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		throw new ReturnException(obj.decoratedEvaluate(context));
	}
}
