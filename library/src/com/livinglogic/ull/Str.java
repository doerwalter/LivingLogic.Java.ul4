package com.livinglogic.ull;

public class Str extends AST
{
	protected String value;

	public Str(int start, int end, String value)
	{
		super(start, end);
		this.value = value;
	}

	public String getTokenType()
	{
		return "str";
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADSTR, r, value, location);
		return r;
	}

	public String toString()
	{
		return "string \"" + value.replace("\"", "\\\"") + "\"";
	}
}
