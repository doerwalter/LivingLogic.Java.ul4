/*
** Copyright 2009-2011 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public abstract class LoadConst extends AST
{
	public LoadConst(int start, int end)
	{
		super(start, end);
	}

	abstract public int getType();

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(getType(), r, location);
		return r;
	}
}
