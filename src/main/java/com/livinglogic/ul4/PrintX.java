/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class PrintX extends AST
{
	protected AST value;

	public PrintX(AST value)
	{
		this.value = value;
	}

	public String toString()
	{
		return "PrintX(" + value + ")";
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		return -1;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(Utils.xmlescape(value.evaluate(context)));
		return null;
	}
}
