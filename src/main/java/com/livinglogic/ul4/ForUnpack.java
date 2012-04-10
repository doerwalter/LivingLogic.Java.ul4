/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;

public class ForUnpack extends AST
{
	protected LinkedList<String> iternames;
	protected AST container;

	public ForUnpack(AST container)
	{
		this.iternames = null;
		this.container = container;
	}

	public ForUnpack()
	{
		this.iternames = null;
		this.container = null;
	}

	public void append(String itername)
	{
		if (iternames == null)
			iternames = new LinkedList<String>();
		iternames.add(itername);
	}

	public void setContainer(AST container)
	{
		this.container = container;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int rc = container.compile(template, registers, location);
		int ri = registers.alloc();
		template.opcode(Opcode.OC_FOR, ri, rc, location);
		int rii = registers.alloc();
		int i = 0;
		for (String itername : iternames)
		{
			template.opcode(Opcode.OC_LOADINT, rii, Integer.toString(i), location);
			template.opcode(Opcode.OC_GETITEM, rii, ri, rii, location);
			template.opcode(Opcode.OC_STOREVAR, rii, itername, location);
			++i;
		}
		registers.free(rii);
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
