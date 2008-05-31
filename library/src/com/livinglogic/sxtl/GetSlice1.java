package com.livinglogic.sxtl;

public class GetSlice1 extends Binary
{
	public GetSlice1(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public String getOpcode()
	{
		return "getslice1";
	}
}
