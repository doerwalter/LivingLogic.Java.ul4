/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class LoadStr extends LoadConst
{
	protected String value;

	public LoadStr(String value)
	{
		this.value = value;
	}

	public int getType()
	{
		return Opcode.OC_LOADSTR;
	}

	public String getTokenType()
	{
		return "str";
	}

	public Object getValue()
	{
		return value;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADSTR, r, value, location);
		return r;
	}

	public String toString()
	{
		return Utils.repr(value);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return value;
	}
}
