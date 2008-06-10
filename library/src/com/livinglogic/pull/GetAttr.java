package com.livinglogic.pull;

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

	public int compile(Template template, Registers registers, Location location)
	{
		int r = obj.compile(template, registers, location);
		template.opcode(Opcode.OC_GETATTR, r, r, attr.value, location);
		return r;
	}
}
