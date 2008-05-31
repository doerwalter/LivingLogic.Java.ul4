package com.livinglogic.sxtl;

public class Name extends AST
{
	protected String value;

	public Name(int start, int end, String value)
	{
		super(start, end);
		this.value = value;
	}

	public int compile(Template template, Registers registers, Template.Location location)
	{
		int r = registers.alloc();
		template.opcode("loadvar", r, value, location);
		return r;
	}
}
