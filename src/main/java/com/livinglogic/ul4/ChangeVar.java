/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

abstract class ChangeVar extends AST
{
	protected String varname;
	protected AST value;

	public ChangeVar(String varname, AST value)
	{
		this.varname = varname;
		this.value = value;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = value.compile(template, registers, location);
		template.opcode(getType(), r, varname, location);
		registers.free(r);
		return -1;
	}

	abstract int getType();

	public String toString()
	{
		return Opcode.code2name(getType()) + "(" + varname + ", " + value + ")";
	}
}
