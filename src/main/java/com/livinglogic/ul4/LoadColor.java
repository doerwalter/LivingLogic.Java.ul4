/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class LoadColor extends LoadConst
{
	protected Color value;

	public LoadColor(Color value)
	{
		this.value = value;
	}

	public int getType()
	{
		return Opcode.OC_LOADCOLOR;
	}

	public String getTokenType()
	{
		return "color";
	}

	public Object getValue()
	{
		return value;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();

		template.opcode(Opcode.OC_LOADCOLOR, r, value.dump(), location);
		return r;
	}

	public String toString()
	{
		return Utils.repr(value);
	}
}
