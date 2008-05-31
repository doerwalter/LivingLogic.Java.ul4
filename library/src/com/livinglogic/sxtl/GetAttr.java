package com.livinglogic.sxtl;

public class GetAttr extends AST
{
	protected AST obj;
	protected Name attr;

	public GetAttr(int start, int end, AST obj, Name attr)
	{
		super(start, end);
		this.obj = obj;
		this.attr = attr;
	}

	public String getType()
	{
		return "getattr";
	}

	public int compile(Template template, Registers registers, Template.Location location)
	{
		int r = obj.compile(template, registers, location);
		template.opcode("getattr", r, r, attr.value, location);
		return r;
	}
}
