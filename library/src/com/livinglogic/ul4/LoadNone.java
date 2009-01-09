package com.livinglogic.ul4;

public class LoadNone extends LoadConst
{
	public LoadNone(int start, int end)
	{
		super(start, end);
	}

	public int getType()
	{
		return Opcode.OC_LOADNONE;
	}

	public String getTokenType()
	{
		return "none";
	}

	public Object getValue()
	{
		return null;
	}

	public String toString()
	{
		return "constant None";
	}
}