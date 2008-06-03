package com.livinglogic.sxtl;

public class CallMeth extends AST
{
	protected Name name;
	protected AST obj;
	protected AST arg1;
	protected AST arg2;
	protected AST arg3;
	protected int argcount;

	public CallMeth(int start, int end, AST obj, Name name, AST arg1, AST arg2, AST arg3)
	{
		super(start, end);
		this.obj = obj;
		this.name = name;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
		this.argcount = 3;
	}

	public CallMeth(int start, int end, AST obj, Name name, AST arg1, AST arg2)
	{
		super(start, end);
		this.obj = obj;
		this.name = name;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = null;
		this.argcount = 2;
	}

	public CallMeth(int start, int end, AST obj, Name name, AST arg1)
	{
		super(start, end);
		this.obj = obj;
		this.name = name;
		this.arg1 = arg1;
		this.arg2 = null;
		this.arg3 = null;
		this.argcount = 1;
	}

	public CallMeth(int start, int end, AST obj, Name name)
	{
		super(start, end);
		this.obj = obj;
		this.name = name;
		this.arg1 = null;
		this.arg2 = null;
		this.arg3 = null;
		this.argcount = 0;
	}


	private static final int[] opcodes = {Opcode.OC_CALLMETH0, Opcode.OC_CALLMETH1, Opcode.OC_CALLMETH2, Opcode.OC_CALLMETH3};

	public int compile(Template template, Registers registers, Location location)
	{
		int ro = obj.compile(template, registers, location);
		int r1 = arg1 != null ? arg1.compile(template, registers, location) : -1;
		int r2 = arg2 != null ? arg2.compile(template, registers, location) : -1;
		int r3 = arg3 != null ? arg3.compile(template, registers, location) : -1;
		template.opcode(opcodes[argcount], ro, ro, r1, r2, r3, name.value, location);
		if (r1 != -1)
			registers.free(r1);
		if (r2 != -1)
			registers.free(r2);
		if (r3 != -1)
			registers.free(r3);
		return ro;
	}
}
