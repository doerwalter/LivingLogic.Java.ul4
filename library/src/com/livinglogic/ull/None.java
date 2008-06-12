package com.livinglogic.ull;

public class None extends Const
{
	public None(int start, int end)
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

	public String toString()
	{
		return "constant None";
	}
}