/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

/**
 * {@code Print} is an unary AST node that writes a string version of its
 * operand to the output stream.
 */
public class Print extends Unary
{
	public Print(Location location, int start, int end, AST obj)
	{
		super(location, start, end, obj);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("print ");
		super.toString(formatter);
	}

	public String getType()
	{
		return "print";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(FunctionStr.call(obj.decoratedEvaluate(context)));
		return null;
	}
}
