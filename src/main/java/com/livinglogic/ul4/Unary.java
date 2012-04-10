/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

abstract class Unary extends AST
{
	protected AST obj;

	public Unary(AST obj)
	{
		this.obj = obj;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = obj.compile(template, registers, location);
		template.opcode(getType(), r, r, location);
		return r;
	}

	public abstract int getType();
}
