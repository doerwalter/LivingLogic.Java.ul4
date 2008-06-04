package com.livinglogic.ull;

abstract class ChangeVar extends AST
{
	protected Name name;
	protected AST value;

	public ChangeVar(int start, int end, Name name, AST value)
	{
		super(start, end);
		this.name = name;
		this.value = value;
	}

	abstract public int getType();

	public int compile(Template template, Registers registers, Location location)
	{
		int r = value.compile(template, registers, location);
		template.opcode(getType(), r, name.value, location);
		registers.free(r);
		return -1;
	}
}
