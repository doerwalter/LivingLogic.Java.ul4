/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

/**
 * {@code PrintX} is an unary AST node that writes a string version of its
 * operand to the output stream and replaces the characters {@code <}, {@code >},
 * {@code &}, {@code '} and {@code "} with the appropriate XML character
 * entities.
 */
public class PrintX extends Unary
{
	public PrintX(Location location, int start, int end, AST obj)
	{
		super(location, start, end, obj);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("printx ");
		super.toString(formatter);
	}

	public String getType()
	{
		return "printx";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(FunctionXMLEscape.call(obj.decoratedEvaluate(context)));
		return null;
	}
}
