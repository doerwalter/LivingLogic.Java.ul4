/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class AddVar extends ChangeVar
{
	public AddVar(Name name, AST value)
	{
		super(name, value);
	}

	public int getType()
	{
		return Opcode.OC_ADDVAR;
	}
}
