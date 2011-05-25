package com.livinglogic.ul4;

public class StoreVar extends ChangeVar
{
	public StoreVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public int getType()
	{
		return Opcode.OC_STOREVAR;
	}
}
