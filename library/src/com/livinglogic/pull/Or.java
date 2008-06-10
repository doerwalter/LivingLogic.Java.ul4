package com.livinglogic.pull;

public class Or extends Binary
{
	public Or(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_OR;
	}
}
