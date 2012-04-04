/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ModVar extends ChangeVar
{
	public ModVar(Name name, AST value)
	{
		super(name, value);
	}

	public int getType()
	{
		return Opcode.OC_MODVAR;
	}
}
