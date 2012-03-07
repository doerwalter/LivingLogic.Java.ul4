/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class For4 extends AST
{
	protected Name iter1;
	protected Name iter2;
	protected Name iter3;
	protected Name iter4;
	protected AST container;

	public For4(int start, int end, Name iter1, Name iter2, Name iter3, Name iter4, AST container)
	{
		super(start, end);
		this.iter1 = iter1;
		this.iter2 = iter2;
		this.iter3 = iter3;
		this.iter4 = iter4;
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
		template.opcode(Opcode.OC_STOREVAR, rii, iter1.value, location);
		template.opcode(Opcode.OC_LOADINT, rii, "1", location);
		template.opcode(Opcode.OC_GETITEM, rii, ri, rii, location);
		template.opcode(Opcode.OC_STOREVAR, rii, iter2.value, location);
		template.opcode(Opcode.OC_LOADINT, rii, "2", location);
		template.opcode(Opcode.OC_GETITEM, rii, ri, rii, location);
		template.opcode(Opcode.OC_STOREVAR, rii, iter3.value, location);
		template.opcode(Opcode.OC_LOADINT, rii, "3", location);
		template.opcode(Opcode.OC_GETITEM, rii, ri, rii, location);
		template.opcode(Opcode.OC_STOREVAR, rii, iter4.value, location);
		registers.free(rii);
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
