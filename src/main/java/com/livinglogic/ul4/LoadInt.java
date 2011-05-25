package com.livinglogic.ul4;

public class LoadInt extends LoadConst
{
	protected int value;

	public LoadInt(int start, int end, int value)
	{
		super(start, end);
		this.value = value;
	}

	public int getType()
	{
		return Opcode.OC_LOADINT;
	}

	public String getTokenType()
	{
		return "int";
	}

	public Object getValue()
	{
		return new Integer(value);
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADINT, r, String.valueOf(value), location);
		return r;
	}

	public String toString()
	{
		return "constant " + value;
	}
}
