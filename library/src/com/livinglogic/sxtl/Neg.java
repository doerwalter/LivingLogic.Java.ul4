package com.livinglogic.sxtl;

public class Neg extends Unary
{
	public Neg(int start, int end, AST obj)
	{
		super(start, end, obj);
	}

	public String getType()
	{
		return "neg";
	}
}