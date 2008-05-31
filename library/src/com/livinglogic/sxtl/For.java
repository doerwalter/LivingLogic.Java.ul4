package com.livinglogic.sxtl;

public class For extends AST
{
	protected Name iter;
	protected AST container;

	public For(int start, int end, Name iter, AST container)
	{
		super(start, end);
		this.iter = iter;
		this.container = container;
	}

	public String getType()
	{
		return "for";
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int rc = container.compile(template, registers, location);
		int ri = registers.alloc();
		template.opcode("for", ri, rc, location);
		template.opcode("storevar", ri, iter.value, location);
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
