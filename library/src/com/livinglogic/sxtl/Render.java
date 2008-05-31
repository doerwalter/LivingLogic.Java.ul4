package com.livinglogic.sxtl;

public class Render extends AST
{
	protected Name name;
	protected AST obj;

	public Render(int start, int end, Name name, AST obj)
	{
		super(start, end);
		this.name = name;
		this.obj = obj;
	}

	public String getType()
	{
		return "render";
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = obj.compile(template, registers, location);
		template.opcode("render", r, name.value, location);
		registers.free(r);
		return -1;
	}
}
