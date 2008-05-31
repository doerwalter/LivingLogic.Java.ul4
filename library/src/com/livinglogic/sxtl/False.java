package com.livinglogic.sxtl;

public class False extends Const
{
	public False(int start, int end)
	{
		super(start, end);
	}

	public Opcode.Type getType()
	{
		return Opcode.Type.LOADFALSE;
	}

	public String getTokenType()
	{
		return "false";
	}
}