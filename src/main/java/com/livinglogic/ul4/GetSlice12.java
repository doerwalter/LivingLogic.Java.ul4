/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class GetSlice12 extends AST
{
	protected AST obj;
	protected AST index1;
	protected AST index2;

	public GetSlice12(int start, int end, AST obj, AST index1, AST index2)
	{
		super(start, end);
		this.obj = obj;
		this.index1 = index1;
		this.index2 = index2;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = obj.compile(template, registers, location);
		int r1 = index1.compile(template, registers, location);
		int r2 = index2.compile(template, registers, location);
		template.opcode(Opcode.OC_GETSLICE12, r, r, r1, r2, location);
		return r;
	}
}
