/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;

public class For extends AST
{
	protected String itername;
	protected AST container;

	public For(String itername, AST container)
	{
		this.itername = itername;
		this.container = container;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int rc = container.compile(template, registers, location);
		int ri = registers.alloc();
		template.opcode(Opcode.OC_FOR, ri, rc, location);

		template.opcode(Opcode.OC_STOREVAR, ri, itername, location);
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
