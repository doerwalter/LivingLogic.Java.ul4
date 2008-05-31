package com.livinglogic.sxtl;

public class For1 extends AST
{
	protected Name iter;
	protected AST container;

	public For1(int start, int end, Name iter, AST container)
	{
		super(start, end);
		this.iter = iter;
		this.container = container;
	}

	public String getType()
	{
		return "for1";
	}

	public int compile(Template template, Registers registers, Template.Location location)
	{
		int rc = container.compile(template, registers, location);
		int ri = registers.alloc();
		template.opcode("for", ri, rc, location);
		int rii = registers.alloc();
		template.opcode("loatint", rii, "0", location);
		template.opcode("getitem", rii, ri, rii, location);
		template.opcode("storevar", rii, iter.value, location);
		registers.free(rii);
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
