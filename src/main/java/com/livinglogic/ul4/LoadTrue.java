/*
** Copyright 2009-2011 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class LoadTrue extends LoadConst
{
	public LoadTrue(int start, int end)
	{
		super(start, end);
	}

	public int getType()
	{
		return Opcode.OC_LOADTRUE;
	}

	public String getTokenType()
	{
		return "true";
	}

	public Object getValue()
	{
		return Boolean.TRUE;
	}

	public String toString()
	{
		return "constant True";
	}
}
