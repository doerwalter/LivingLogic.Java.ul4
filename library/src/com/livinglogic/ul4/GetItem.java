package com.livinglogic.ul4;

public class GetItem extends Binary
{
	public GetItem(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_GETITEM;
	}
}
