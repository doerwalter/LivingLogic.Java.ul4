package com.livinglogic.ul4;

public class LoadColor extends AST
{
	protected Color value;

	public LoadColor(int start, int end, Color value)
	{
		super(start, end);
		this.value = value;
	}

	public String getTokenType()
	{
		return "color";
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		
		template.opcode(Opcode.OC_LOADCOLOR, r, value.dump(), location);
		return r;
	}

	public String toString()
	{
		return "constant " + value;
	}
}
