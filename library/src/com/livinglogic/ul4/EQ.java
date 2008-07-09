package com.livinglogic.ul4;

public class EQ extends Binary
{
	public EQ(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_EQ;
	}
}