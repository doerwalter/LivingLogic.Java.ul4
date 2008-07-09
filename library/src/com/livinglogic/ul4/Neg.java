package com.livinglogic.ul4;

public class Neg extends Unary
{
	public Neg(int start, int end, AST obj)
	{
		super(start, end, obj);
	}

	public int getType()
	{
		return Opcode.OC_NEG;
	}
}