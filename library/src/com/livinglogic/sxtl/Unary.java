package com.livinglogic.sxtl;

class Unary extends AST
{
	protected AST obj;

	public Unary(int start, int end, AST obj)
	{
		super(start, end);
		this.obj = obj;
	}

	public String getOpcode()
	{
		return null;
	}

	public int compile(Template template, Registers registers, Template.Location location)
	{
		int r = obj.compile(template, registers, location);
		template.opcode(getOpcode(), r, r, location);
		return r;
	}
}
