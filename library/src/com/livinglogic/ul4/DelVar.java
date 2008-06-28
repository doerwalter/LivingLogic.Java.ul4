package com.livinglogic.ul4;

public class DelVar extends AST
{
	protected Name name;
	protected AST value;

	public DelVar(int start, int end, Name name)
	{
		super(start, end);
		this.name = name;
	}

	public int compile(Template template, Registers registers, Location location)
	{
		template.opcode(Opcode.OC_DELVAR, name.value, location);
		return -1;
	}
}
