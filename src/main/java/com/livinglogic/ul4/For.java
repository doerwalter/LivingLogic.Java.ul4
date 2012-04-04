/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;

public class For extends AST
{
	protected String itername; // variable name for normal ``for varname in container`` loop
	protected LinkedList<String> unpackediternames; // list of variable names for list with iterator unpacking i.e. something like ``for (a, b) in container``
	protected AST container;

	public For(String itername, AST container)
	{
		this.itername = itername;
		this.unpackediternames = null;
		this.container = container;
	}

	public For(AST container)
	{
		this.itername = null;
		this.unpackediternames = null;
		this.container = container;
	}

	public void append(String unpackeditername)
	{
		if (itername != null)
			throw new RuntimeException("can't add unpacked for variable");
		if (unpackediternames == null)
			unpackediternames = new LinkedList<String>();
		unpackediternames.add(unpackeditername);
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int rc = container.compile(template, registers, location);
		int ri = registers.alloc();
		template.opcode(Opcode.OC_FOR, ri, rc, location);

		if (itername != null)
		{
			// normal loop
			template.opcode(Opcode.OC_STOREVAR, ri, itername, location);
		}
		else
		{
			// use unpacking
			int rii = registers.alloc();
			int i = 0;
			for (String itername : unpackediternames)
			{
				template.opcode(Opcode.OC_LOADINT, rii, Integer.toString(i), location);
				template.opcode(Opcode.OC_GETITEM, rii, ri, rii, location);
				template.opcode(Opcode.OC_STOREVAR, rii, itername, location);
				++i;
			}
			registers.free(rii);
		}
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
