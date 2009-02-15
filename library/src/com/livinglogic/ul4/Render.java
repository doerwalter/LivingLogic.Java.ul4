package com.livinglogic.ul4;

import java.util.LinkedList;

public class Render extends AST
{
	protected AST template;
	protected LinkedList args = new LinkedList();

	public Render(int start, int end, AST template)
	{
		super(start, end);
		this.template = template;
	}

	public void append(String name, AST value)
	{
		args.add(new KeywordArg(name, value));
	}

	public void append(AST value)
	{
		args.add(new KeywordArg(null, value));
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int ra = registers.alloc();
		template.opcode(Opcode.OC_BUILDDICT, ra, location);
		int argCount = args.size();
		for (int i = 0; i < argCount; ++i)
		{
			KeywordArg arg = (KeywordArg)args.get(i);
			int rv = arg.value.compile(template, registers, location);
			if (arg.name == null)
			{
				template.opcode(Opcode.OC_UPDATEDICT, ra, rv, location);
			}
			else
			{
				int rk = registers.alloc();
				template.opcode(Opcode.OC_LOADSTR, rk, arg.name, location);
				template.opcode(Opcode.OC_ADDDICT, ra, rk, rv, location);
				registers.free(rk);
			}
			registers.free(rv);
		}
		int rt = this.template.compile(template, registers, location);
		template.opcode(Opcode.OC_RENDER, rt, ra, location);
		registers.free(rt);
		registers.free(ra);
		return -1;
	}
}
