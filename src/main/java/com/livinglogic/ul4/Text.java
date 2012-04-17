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

	public String name()
	{
		return "text";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(text);
		return null;
	}
}
