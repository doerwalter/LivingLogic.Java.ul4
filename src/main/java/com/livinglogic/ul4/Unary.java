/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class Unary extends AST
{
	protected int type;
	protected AST obj;

	public Unary(int type, AST obj)
	{
		this.type = type;
		this.obj = obj;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = obj.compile(template, registers, location);
		template.opcode(type, r, r, location);
		return r;
	}
}
