package com.livinglogic.pull;

public class TrueDivVar extends ChangeVar
{
	public TrueDivVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public int getType()
	{
		return Opcode.OC_TRUEDIVVAR;
	}
}
