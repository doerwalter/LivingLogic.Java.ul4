package com.livinglogic.sxtl;

public class Int extends AST
{
	protected int value;

	public Int(int start, int end, int value)
	{
		super(start, end);
		this.value = value;
	}

	public String getTokenType()
	{
		return "int";
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADINT, r, String.valueOf(value), location);
		return r;
	}
}
