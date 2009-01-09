package com.livinglogic.ul4;

public class LoadDate extends AST
{
	protected java.util.Date value;

	public LoadDate(int start, int end, java.util.Date value)
	{
		super(start, end);
		this.value = value;
	}

	public String getTokenType()
	{
		return "date";
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADDATE, r, (String)Utils.isoformat(value), location);
		return r;
	}

	public String toString()
	{
		return "constant " + value;
	}
}
