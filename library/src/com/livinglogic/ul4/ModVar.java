package com.livinglogic.ul4;

public class ModVar extends ChangeVar
{
	public ModVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public int getType()
	{
		return Opcode.OC_MODVAR;
	}
}
