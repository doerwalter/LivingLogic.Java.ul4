package com.livinglogic.ul4;

public abstract class Const extends AST
{
	public Const(int start, int end)
	{
		super(start, end);
	}

	abstract public int getType();

	abstract public Object getValue();

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(getType(), r, location);
		return r;
	}	
}