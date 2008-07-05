package com.livinglogic.ul4;

import java.util.LinkedList;

public class Render extends AST
{
	protected Name name;
	protected LinkedList args = new LinkedList();

	public Render(int start, int end, Name name)
	{
		super(start, end);
		this.name = name;
	}

	public void append(String name, AST value)
	{
		args.add(new RenderArg(name, value));
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int ra = registers.alloc();
		template.opcode(Opcode.OC_BUILDDICT, ra, location);
		int argCount = args.size();
		for (int i = 0; i < argCount; ++i)
		{
			RenderArg arg = (RenderArg)args.get(i);
			int rv = arg.value.compile(template, registers, location);
			int rk = registers.alloc();
			template.opcode(Opcode.OC_LOADSTR, rk, arg.name, location);
			template.opcode(Opcode.OC_ADDDICT, ra, rk, rv, location);
			registers.free(rk);
			registers.free(rv);
		}
		template.opcode(Opcode.OC_RENDER, ra, name.value, location);
		return -1;
	}
}
