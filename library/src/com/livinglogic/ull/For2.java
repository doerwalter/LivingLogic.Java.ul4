package com.livinglogic.ull;

public class For2 extends AST
{
	protected Name iter1;
	protected Name iter2;
	protected AST container;

	public For2(int start, int end, Name iter1, Name iter2, AST container)
	{
		super(start, end);
		this.iter1 = iter1;
		this.iter2 = iter2;
		this.container = container;
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int rc = container.compile(template, registers, location);
		int ri = registers.alloc();
		template.opcode(Opcode.OC_FOR, ri, rc, location);
		int rii = registers.alloc();
		template.opcode(Opcode.OC_LOADINT, rii, "0", location);
		template.opcode(Opcode.OC_GETITEM, rii, ri, rii, location);
		template.opcode(Opcode.OC_STOREVAR, rii, iter1.value, location);
		template.opcode(Opcode.OC_LOADINT, rii, "1", location);
		template.opcode(Opcode.OC_GETITEM, rii, ri, rii, location);
		template.opcode(Opcode.OC_STOREVAR, rii, iter2.value, location);
		registers.free(rii);
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
