package com.livinglogic.pull;

public class FloorDivVar extends ChangeVar
{
	public FloorDivVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public int getType()
	{
		return Opcode.OC_FLOORDIVVAR;
	}
}
