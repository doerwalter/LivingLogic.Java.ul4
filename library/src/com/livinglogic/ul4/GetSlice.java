package com.livinglogic.ul4;

public class GetSlice extends Unary
{
	public GetSlice(int start, int end, AST obj)
	{
		super(start, end, obj);
	}

	public int getType()
	{
		return Opcode.OC_GETSLICE;
	}
}
