package com.livinglogic.sxtl;

public class Add extends Binary
{
	public Add(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public String getType()
	{
		return "add";
	}
}