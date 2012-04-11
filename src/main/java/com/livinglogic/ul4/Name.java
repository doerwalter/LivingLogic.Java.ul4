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

	public String getValue()
	{
		return value;
	}

	public String getTokenType()
	{
		return "name";
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADVAR, r, value, location);
		return r;
	}

	public String toString()
	{
		return value;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return context.get(value);
	}
}
