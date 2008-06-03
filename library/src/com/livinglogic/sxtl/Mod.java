package com.livinglogic.sxtl;

public class Mod extends Binary
{
	public Mod(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_MOD;
	}
}
