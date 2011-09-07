/*
** Copyright 2009-2011 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class LoadFalse extends LoadConst
{
	public LoadFalse(int start, int end)
	{
		super(start, end);
	}

	public int getType()
	{
		return Opcode.OC_LOADFALSE;
	}

	public String getTokenType()
	{
		return "false";
	}

	public Object getValue()
	{
		return Boolean.FALSE;
	}

	public String toString()
	{
		return "constant False";
	}
}
