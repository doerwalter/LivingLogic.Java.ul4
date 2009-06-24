package com.livinglogic.ul4;

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

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int rc = container.compile(template, registers, location);
		int ri = registers.alloc();
		template.opcode(Opcode.OC_FOR, ri, rc, location);
		template.opcode(Opcode.OC_STOREVAR, ri, iter.value, location);
		registers.free(ri);
		registers.free(rc);
		return -1;
	}
}
