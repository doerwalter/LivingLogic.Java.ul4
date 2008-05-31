package com.livinglogic.sxtl;

public class GetSlice extends Unary
{
	public GetSlice(int start, int end, AST obj)
	{
		super(start, end, obj);
	}

	public Opcode.Type getType()
	{
		return Opcode.Type.GETSLICE;
	}
}
