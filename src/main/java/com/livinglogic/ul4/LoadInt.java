/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class LoadInt extends LoadConst
{
	protected Object value;

	public LoadInt(Object value)
	{
		this.value = value;
	}

	public int getType()
	{
		return Opcode.OC_LOADINT;
	}

	public String getTokenType()
	{
		return "int";
	}

	public Object getValue()
	{
		return value;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADINT, r, value.toString(), location);
		return r;
	}

	public String toString()
	{
		return value.toString();
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return value;
	}
}
