package com.livinglogic.ull;

public class And extends Binary
{
	public And(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_AND;
	}
}
