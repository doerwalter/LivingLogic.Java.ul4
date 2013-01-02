/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class UL4Token
{
	protected int start;
	protected int end;
	protected String type;

	public UL4Token(int start, int end, String type)
	{
		this.start = start;
		this.end = end;
		this.type = type;
	}

	public int getStart()
	{
		return start;
	}

	public int getEnd()
	{
		return end;
	}

	public String getTokenType()
	{
		return type;
	}

	public String toString()
	{
		return "token \"" + type.replaceAll("\"", "\\\\\"") + "\"";
	}
}
