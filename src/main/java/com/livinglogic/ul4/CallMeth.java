/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;

public class CallMeth extends AST
{
	protected String name;
	protected AST obj;
	protected LinkedList<AST> args;

	public CallMeth(AST obj, String name)
	{
		this.obj = obj;
		this.name = name;
		this.args = new LinkedList<AST>();
	}

	public void append(AST arg)
	{
		args.add(arg);
	}

	private static final int[] opcodes = {Opcode.OC_CALLMETH0, Opcode.OC_CALLMETH1, Opcode.OC_CALLMETH2, Opcode.OC_CALLMETH3};

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int argcount = args.size();

		if (argcount > 3)
			throw new RuntimeException(argcount + " arguments not supported by CallMeth");

		int ro = obj.compile(template, registers, location);
		int r1 = argcount>0 ? args.get(0).compile(template, registers, location) : -1;
		int r2 = argcount>1 ? args.get(1).compile(template, registers, location) : -1;
		int r3 = argcount>2 ? args.get(2).compile(template, registers, location) : -1;
		template.opcode(opcodes[argcount], ro, ro, r1, r2, r3, name, location);
		if (r1 != -1)
			registers.free(r1);
		if (r2 != -1)
			registers.free(r2);
		if (r3 != -1)
			registers.free(r3);
		return ro;
	}
}
