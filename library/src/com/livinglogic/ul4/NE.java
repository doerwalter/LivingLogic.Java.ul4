package com.livinglogic.ul4;

public class NE extends Binary
{
	public NE(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_NE;
	}
}
