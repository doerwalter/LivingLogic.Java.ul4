package com.livinglogic.ull;

public class SubVar extends ChangeVar
{
	public SubVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public int getType()
	{
		return Opcode.OC_SUBVAR;
	}
}
