package com.livinglogic.pull;

public abstract class Const extends AST
{
	public Const(int start, int end)
	{
		super(start, end);
	}

	abstract public int getType();

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(getType(), r, location);
		return r;
	}	
}