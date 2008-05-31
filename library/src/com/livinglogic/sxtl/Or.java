package com.livinglogic.sxtl;

public class Or extends Binary
{
	public Or(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public Opcode.Type getType()
	{
		return Opcode.Type.OR;
	}
}
