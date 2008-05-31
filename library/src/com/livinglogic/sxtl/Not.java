package com.livinglogic.sxtl;

public class Not extends Unary
{
	public Not(int start, int end, AST obj)
	{
		super(start, end, obj);
	}

	public Opcode.Type getType()
	{
		return Opcode.Type.NOT;
	}
}
