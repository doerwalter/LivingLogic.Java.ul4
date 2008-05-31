package com.livinglogic.sxtl;

public class Float extends AST
{
	protected double value;

	public Float(int start, int end, double value)
	{
		super(start, end);
		this.value = value;
	}

	public String getType()
	{
		return "float";
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode("loadfloat", r, String.valueOf(value), location);
		return r;
	}
}
