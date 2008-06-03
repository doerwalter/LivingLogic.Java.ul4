package com.livinglogic.sxtl;

abstract class Binary extends AST
{
	protected AST obj1;
	protected AST obj2;

	public Binary(int start, int end, AST obj1, AST obj2)
	{
		super(start, end);
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	abstract public int getType();

	public int compile(Template template, Registers registers, Location location)
	{
		int r1 = obj1.compile(template, registers, location);
		int r2 = obj2.compile(template, registers, location);
		template.opcode(getType(), r1, r1, r2, location);
		registers.free(r2);
		return r1;
	}
}
