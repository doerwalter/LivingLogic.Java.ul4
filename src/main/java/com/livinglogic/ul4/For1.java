/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class For1 extends AST
{
	protected Name iter;
	protected AST container;

	public For1(int start, int end, Name iter, AST container)
	{
		super(start, end);
		this.iter = iter;
		this.container = container;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int rc = container.compile(template, registers, location);
		int ri = registers.alloc();
		template.opcode(Opcode.OC_FOR, ri, rc, location);
		int rii = registers.alloc();
		template.opcode(Opcode.OC_LOADINT, rii, "0", location);
		template.opcode(Opcode.OC_GETITEM, rii, ri, rii, location);
		template.opcode(Opcode.OC_STOREVAR, rii, iter.value, location);
		registers.free(rii);
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
