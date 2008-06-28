package com.livinglogic.ul4;

public class MulVar extends ChangeVar
{
	public MulVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public int getType()
	{
		return Opcode.OC_MULVAR;
	}
}
