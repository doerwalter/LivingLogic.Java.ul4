package com.livinglogic.sxtl;

public class Name extends AST
{
	protected String value;

	public Name(int start, int end, String value)
	{
		super(start, end);
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public String getTokenType()
	{
		return "name";
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADVAR, r, value, location);
		return r;
	}
}
