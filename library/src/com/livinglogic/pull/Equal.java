package com.livinglogic.pull;

public class Equal extends Binary
{
	public Equal(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_EQUALS;
	}
}
