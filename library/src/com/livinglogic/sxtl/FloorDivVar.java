package com.livinglogic.sxtl;

public class FloorDivVar extends ChangeVar
{
	public FloorDivVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public Opcode.Type getType()
	{
		return Opcode.Type.FLOORDIVVAR;
	}
}
