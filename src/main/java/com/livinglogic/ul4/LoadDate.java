/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class LoadDate extends LoadConst
{
	protected java.util.Date value;

	public LoadDate(java.util.Date value)
	{
		this.value = value;
	}

	public int getType()
	{
		return Opcode.OC_LOADDATE;
	}

	public String getTokenType()
	{
		return "date";
	}

	public Object getValue()
	{
		return value;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADDATE, r, (String)Utils.isoformat(value), location);
		return r;
	}

	public String toString()
	{
		return Utils.repr(value);
	}
}
