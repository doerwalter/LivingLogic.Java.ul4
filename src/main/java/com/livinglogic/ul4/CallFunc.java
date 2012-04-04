/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;

public class CallFunc extends AST
{
	protected String name;
	protected LinkedList<AST> args;

	public CallFunc(String name)
	{
		this.name = name;
		this.args = new LinkedList<AST>();
	}

	public void append(AST arg)
	{
		args.add(arg);
	}

	private static final int[] opcodes = {Opcode.OC_CALLFUNC0, Opcode.OC_CALLFUNC1, Opcode.OC_CALLFUNC2, Opcode.OC_CALLFUNC3, Opcode.OC_CALLFUNC4};

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int argcount = args.size();

		int r1 = argcount>0 ? args.get(0).compile(template, registers, location) : -1;
		int r2 = argcount>1 ? args.get(1).compile(template, registers, location) : -1;
		int r3 = argcount>2 ? args.get(2).compile(template, registers, location) : -1;
		int r4 = argcount>3 ? args.get(3).compile(template, registers, location) : -1;
		int rr = argcount > 0 ? r1 : registers.alloc();
		template.opcode(opcodes[argcount], rr, r1, r2, r3, r4, name, location);
		if (r2 != -1)
			registers.free(r2);
		if (r3 != -1)
			registers.free(r3);
		if (r4 != -1)
			registers.free(r4);
		return rr;
	}
}
