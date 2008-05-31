package com.livinglogic.sxtl;

public class Str extends AST
{
	protected String value;

	public Str(int start, int end, String value)
	{
		super(start, end);
		this.value = value;
	}

	public String getType()
	{
		return "str";
	}

	public int compile(Template template, Registers registers, Template.Location location)
	{
		int r = registers.alloc();
		template.opcode("loadstr", r, value, location);
		return r;
	}
}
