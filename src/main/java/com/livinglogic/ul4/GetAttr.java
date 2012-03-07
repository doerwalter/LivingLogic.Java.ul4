/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class GetAttr extends AST
{
	protected AST obj;
	protected Name attr;

	public GetAttr(int start, int end, AST obj, Name attr)
	{
		super(start, end);
		this.obj = obj;
		this.attr = attr;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = obj.compile(template, registers, location);
		template.opcode(Opcode.OC_GETATTR, r, r, attr.value, location);
		return r;
	}
}
