/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Text extends AST
{
	protected String text;

	public Text(String text)
	{
		this.text = text;
	}

	public String toString()
	{
		return "Text(" + Utils.repr(text) + ")";
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		return -1;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(text);
		return null;
	}
}
