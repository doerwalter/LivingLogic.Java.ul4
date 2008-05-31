package com.livinglogic.sxtl;

public class Contains extends Binary
{
	public Contains(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public String getOpcode()
	{
		return "contains";
	}
}
