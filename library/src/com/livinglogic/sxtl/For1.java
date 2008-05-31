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

	public int compile(Template template, Registers registers, Location location)
	{
		int rc = container.compile(template, registers, location);
		int ri = registers.alloc();
		template.opcode(Opcode.Type.FOR, ri, rc, location);
		int rii = registers.alloc();
		template.opcode(Opcode.Type.LOADINT, rii, "0", location);
		template.opcode(Opcode.Type.GETITEM, rii, ri, rii, location);
		template.opcode(Opcode.Type.STOREVAR, rii, iter.value, location);
		registers.free(rii);
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
