package com.livinglogic.ull;

public class Not extends Unary
{
	public Not(int start, int end, AST obj)
	{
		super(start, end, obj);
	}

	public int getType()
	{
		return Opcode.OC_NOT;
	}
}
