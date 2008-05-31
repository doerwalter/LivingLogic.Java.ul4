package com.livinglogic.sxtl;

class Const extends AST
{
	public Const(int start, int end)
	{
		super(start, end);
	}

	public String getType()
	{
		return null;
	}

	public int compile(Template template, Registers registers, Template.Location location)
	{
		int r = registers.alloc();
		template.opcode("load"+getType(), r, location);
		return r;
	}	
}