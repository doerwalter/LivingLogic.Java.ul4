/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class GetSlice extends AST
{
	protected AST obj;
	protected AST index1;
	protected AST index2;

	public GetSlice(AST obj, AST index1, AST index2)
	{
		this.obj = obj;
		this.index1 = index1;
		this.index2 = index2;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = obj.compile(template, registers, location);
		if (index1 != null)
		{
			if (index2 != null)
			{
				int r1 = index1.compile(template, registers, location);
				int r2 = index2.compile(template, registers, location);
				template.opcode(Opcode.OC_GETSLICE12, r, r, r1, r2, location);
				registers.free(r1);
				registers.free(r2);
			}
			else
			{
				int r1 = index1.compile(template, registers, location);
				template.opcode(Opcode.OC_GETSLICE1, r, r, r1, location);
				registers.free(r1);
			}
		}
		else
		{
			if (index2 != null)
			{
				int r1 = index2.compile(template, registers, location);
				template.opcode(Opcode.OC_GETSLICE2, r, r, r1, location);
				registers.free(r1);
			}
			else
			{
				template.opcode(Opcode.OC_GETSLICE, r, r, location);
			}
		}
		return r;
	}
}
