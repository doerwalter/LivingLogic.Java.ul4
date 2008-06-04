package com.livinglogic.ull;

public class AddVar extends ChangeVar
{
	public AddVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public int getType()
	{
		return Opcode.OC_ADDVAR;
	}
}
