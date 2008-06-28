package com.livinglogic.ul4;

public class GetSlice1 extends Binary
{
	public GetSlice1(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_GETSLICE1;
	}
}
