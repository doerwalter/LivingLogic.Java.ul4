package com.livinglogic.ul4;

public class CallFunc extends AST
{
	protected Name name;
	protected AST arg1;
	protected AST arg2;
	protected AST arg3;
	protected AST arg4;
	protected int argcount;

	public CallFunc(int start, int end, Name name, AST arg1, AST arg2, AST arg3, AST arg4)
	{
		super(start, end);
		this.name = name;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
		this.arg4 = arg4;
		this.argcount = 4;
	}

	public CallFunc(int start, int end, Name name, AST arg1, AST arg2, AST arg3)
	{
		super(start, end);
		this.name = name;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
		this.arg4 = null;
		this.argcount = 3;
	}

	public CallFunc(int start, int end, Name name, AST arg1, AST arg2)
	{
		super(start, end);
		this.name = name;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = null;
		this.arg4 = null;
		this.argcount = 2;
	}

	public CallFunc(int start, int end, Name name, AST arg1)
	{
		super(start, end);
		this.name = name;
		this.arg1 = arg1;
		this.arg2 = null;
		this.arg3 = null;
		this.arg4 = null;
		this.argcount = 1;
	}

	public CallFunc(int start, int end, Name name)
	{
		super(start, end);
		this.name = name;
		this.arg1 = null;
		this.arg2 = null;
		this.arg3 = null;
		this.arg4 = null;
		this.argcount = 0;
	}

	private static final int[] opcodes = {Opcode.OC_CALLFUNC0, Opcode.OC_CALLFUNC1, Opcode.OC_CALLFUNC2, Opcode.OC_CALLFUNC3, Opcode.OC_CALLFUNC4};

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r1 = arg1 != null ? arg1.compile(template, registers, location) : -1;
		int r2 = arg2 != null ? arg2.compile(template, registers, location) : -1;
		int r3 = arg3 != null ? arg3.compile(template, registers, location) : -1;
		int r4 = arg4 != null ? arg4.compile(template, registers, location) : -1;
		int rr = argcount > 0 ? r1 : registers.alloc();
		template.opcode(opcodes[argcount], rr, r1, r2, r3, r4, name.value, location);
		if (r2 != -1)
			registers.free(r2);
		if (r3 != -1)
			registers.free(r3);
		if (r4 != -1)
			registers.free(r4);
		return rr;
	}
}
