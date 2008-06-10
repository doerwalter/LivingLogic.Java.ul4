package com.livinglogic.pull;

public class NotContains extends Binary
{
	public NotContains(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_NOTCONTAINS;
	}
}
