package com.livinglogic.ul4;

public abstract class LoadConst extends AST
{
	public LoadConst(int start, int end)
	{
		super(start, end);
	}

	abstract public int getType();

	abstract public Object getValue();

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(getType(), r, location);
		return r;
	}	
}