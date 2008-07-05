package com.livinglogic.ul4;

import java.util.LinkedList;

public class List extends AST
{
	protected LinkedList items = new LinkedList();

	public List(int start, int end)
	{
		super(start, end);
	}

	public void append(AST item)
	{
		items.add(item);
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_BUILDLIST, r, location);
		int itemCount = items.size();
		for (int i = 0; i < itemCount; ++i)
		{
			AST item = (AST)items.get(i);
			int ri = item.compile(template, registers, location);
			template.opcode(Opcode.OC_ADDLIST, r, ri, location);
			registers.free(ri);
		}
		return r;
	}
}
