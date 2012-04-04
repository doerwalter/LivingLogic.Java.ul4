/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class StoreVar extends ChangeVar
{
	public StoreVar(Name name, AST value)
	{
		super(name, value);
	}

	public int getType()
	{
		return Opcode.OC_STOREVAR;
	}
}
