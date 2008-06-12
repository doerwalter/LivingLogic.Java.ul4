package com.livinglogic.ull;

public class False extends Const
{
	public False(int start, int end)
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

	public String toString()
	{
		return "constant False";
	}
}