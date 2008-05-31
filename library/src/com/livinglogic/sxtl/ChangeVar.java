package com.livinglogic.sxtl;

class ChangeVar extends AST
{
	protected Name name;
	protected AST value;

	public ChangeVar(int start, int end, Name name, AST value)
	{
		super(start, end);
		this.name = name;
		this.value = value;
	}

	public String getOpcode()
	{
		return null;
	}

	public int compile(Template template, Registers registers, Template.Location location)
	{
		int r = value.compile(template, registers, location);
		template.opcode(getOpcode(), r, name.value, location);
		registers.free(r);
		return -1;
	}
}
