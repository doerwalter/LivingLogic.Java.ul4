/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;

public class Render extends AST
{
	protected AST template;
	protected LinkedList<CallArg> args = new LinkedList<CallArg>();

	public Render(AST template)
	{
		this.template = template;
	}

	public void append(String name, AST value)
	{
		args.add(new CallArgNamed(name, value));
	}

	public void append(AST value)
	{
		args.add(new CallArgDict(value));
	}

	public void append(CallArg arg)
	{
		args.add(arg);
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("render(");
		buffer.append(template.toString());
		for (CallArg arg : args)
		{
			buffer.append(", ");
			buffer.append(arg.toString());
		}
		buffer.append(")");
		return buffer.toString();
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int ra = registers.alloc();
		template.opcode(Opcode.OC_BUILDDICT, ra, location);

		for (CallArg arg : args)
		{
			if (arg instanceof CallArgDict)
			{
				int rv = ((CallArgDict)arg).dict.compile(template, registers, location);
				template.opcode(Opcode.OC_UPDATEDICT, ra, rv, location);
				registers.free(rv);
			}
			else
			{
				int rv = ((CallArgNamed)arg).value.compile(template, registers, location);
				int rk = registers.alloc();
				template.opcode(Opcode.OC_LOADSTR, rk, ((CallArgNamed)arg).name, location);
				template.opcode(Opcode.OC_ADDDICT, ra, rk, rv, location);
				registers.free(rk);
				registers.free(rv);
			}
		}
		int rt = this.template.compile(template, registers, location);
		template.opcode(Opcode.OC_RENDER, rt, ra, location);
		registers.free(rt);
		registers.free(ra);
		return -1;
	}
}
