package com.livinglogic.ul4;

public class True extends Const
{
	public True(int start, int end)
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

	public String toString()
	{
		return "constant True";
	}
}
