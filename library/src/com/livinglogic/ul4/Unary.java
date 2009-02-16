package com.livinglogic.ul4;

abstract class Unary extends AST
{
	protected AST obj;

	public Unary(int start, int end, AST obj)
	{
		super(start, end);
		this.obj = obj;
	}

	abstract public int getType();

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = obj.compile(template, registers, location);
		template.opcode(getType(), r, r, location);
		return r;
	}
}
