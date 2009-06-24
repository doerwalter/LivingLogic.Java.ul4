package com.livinglogic.ul4;

public class LoadStr extends LoadConst
{
	protected String value;

	public LoadStr(int start, int end, String value)
	{
		super(start, end);
		this.value = value;
	}

	public int getType()
	{
		return Opcode.OC_LOADSTR;
	}

	public String getTokenType()
	{
		return "str";
	}

	public Object getValue()
	{
		return value;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADSTR, r, value, location);
		return r;
	}

	public String toString()
	{
		return "string \"" + value.replaceAll("\"", "\\\\\"") + "\"";
	}
}
