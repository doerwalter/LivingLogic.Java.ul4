/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class Name extends AST
{
	protected String value;

	public Name(String value)
	{
		this.value = value;
	}

	public String toString()
	{
		return value;
	}

	public String name()
	{
		return "loadvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return context.get(value);
	}
}
