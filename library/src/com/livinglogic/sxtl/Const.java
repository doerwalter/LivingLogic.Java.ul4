package com.livinglogic.sxtl;

public abstract class Const extends AST
{
	public Const(int start, int end)
	{
		super(start, end);
	}

	abstract public Opcode.Type getType();

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(getType(), r, location);
		return r;
	}	
}