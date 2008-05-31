package com.livinglogic.sxtl;

public class Mul extends Binary
{
	public Mul(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public String getType()
	{
		return "mul";
	}
}
