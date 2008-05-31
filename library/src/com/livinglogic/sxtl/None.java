package com.livinglogic.sxtl;

public class None extends Const
{
	public None(int start, int end)
	{
		super(start, end);
	}

	public Opcode.Type getType()
	{
		return Opcode.Type.LOADNONE;
	}

	public String getTokenType()
	{
		return "none";
	}
}