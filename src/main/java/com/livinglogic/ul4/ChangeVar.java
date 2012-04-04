/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class ChangeVar extends AST
{
	protected int type;
	protected Name name;
	protected AST value;

	public ChangeVar(int type, Name name, AST value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = value.compile(template, registers, location);
		template.opcode(type, r, name.value, location);
		registers.free(r);
		return -1;
	}
}
