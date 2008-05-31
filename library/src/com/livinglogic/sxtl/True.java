package com.livinglogic.sxtl;

public class True extends Const
{
	public True(int start, int end)
	{
		super(start, end);
	}

	public Opcode.Type getType()
	{
		return Opcode.Type.LOADTRUE;
	}

	public String getTokenType()
	{
		return "true";
	}
}
