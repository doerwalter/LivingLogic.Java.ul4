package com.livinglogic.sxtl;

public class DelVar extends AST
{
	protected Name name;
	protected AST value;

	public DelVar(int start, int end, Name name)
	{
		super(start, end);
		this.name = name;
	}

	public String getType()
	{
		return "delvar";
	}

	public int compile(Template template, Registers registers, Template.Location location)
	{
		template.opcode("delvar", name.value, location);
		return -1;
	}
}
