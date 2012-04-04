/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class Not extends Unary
{
	public Not(AST obj)
	{
		super(obj);
	}

	public int getType()
	{
		return Opcode.OC_NOT;
	}
}
