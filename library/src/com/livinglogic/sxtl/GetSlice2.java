package com.livinglogic.sxtl;

public class GetSlice2 extends Binary
{
	public GetSlice2(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public String getOpcode()
	{
		return "getslice2";
	}
}
