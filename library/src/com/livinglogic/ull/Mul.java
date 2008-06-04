package com.livinglogic.ull;

public class Mul extends Binary
{
	public Mul(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_MUL;
	}
}
