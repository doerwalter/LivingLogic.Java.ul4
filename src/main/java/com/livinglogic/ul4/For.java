/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class For extends AST
{
	protected Name iter;
	protected AST container;

	public For(Name iter, AST container)
	{
		this.iter = iter;
		this.container = container;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int rc = container.compile(template, registers, location);
		int ri = registers.alloc();
		template.opcode(Opcode.OC_FOR, ri, rc, location);
		template.opcode(Opcode.OC_STOREVAR, ri, iter.value, location);
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
