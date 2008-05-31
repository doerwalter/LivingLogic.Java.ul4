package com.livinglogic.sxtl;

public class Render extends AST
{
	protected Name name;
	protected AST obj;

	public Render(int start, int end, AST obj, Name name)
	{
		super(start, end);
		this.obj = obj;
		this.name = name;
	}

	public String getType()
	{
		return "render";
	}

	public int compile(Template template, Registers registers, Template.Location location)
	{
		int r = obj.compile(template, registers, location);
		template.opcode("render", r, name.value, location);
		registers.free(r);
		return -1;
	}
}
