package com.livinglogic.ul4;

import java.util.LinkedList;

public class Dict extends AST
{
	protected LinkedList items = new LinkedList();

	public Dict(int start, int end)
	{
		super(start, end);
	}

	public void append(AST key, AST value)
	{
		items.add(new DictEntry(key, value));
	}

	public void append(AST value)
	{
		items.add(new DictEntry(value));
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_BUILDDICT, r, location);
		int itemCount = items.size();
		for (int i = 0; i < itemCount; ++i)
		{
			DictEntry item = (DictEntry)items.get(i);
			int rv = item.value.compile(template, registers, location);
			if (item.isdict)
			{
				template.opcode(Opcode.OC_UPDATEDICT, r, rv, location);
			}
			else
			{
				int rk = item.key.compile(template, registers, location);
				template.opcode(Opcode.OC_ADDDICT, r, rk, rv, location);
				registers.free(rk);
			}
			registers.free(rv);
		}
		return r;
	}
}
