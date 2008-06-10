package com.livinglogic.pull;

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
