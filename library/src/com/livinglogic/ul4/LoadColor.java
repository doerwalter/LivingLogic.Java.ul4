package com.livinglogic.ul4;

public class LoadColor extends AST
{
	protected Color value;

	public LoadColor(int start, int end, Color value)
	{
		super(start, end);
		this.value = value;
	}

	public String getTokenType()
	{
		return "color";
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		
		String sr = Integer.toHexString(value.getR());
		if (sr.length() < 2)
			sr = "0" + sr;

		String sg = Integer.toHexString(value.getG());
		if (sg.length() < 2)
			sg = "0" + sg;

		String sb = Integer.toHexString(value.getB());
		if (sb.length() < 2)
			sb = "0" + sb;

		template.opcode(Opcode.OC_LOADCOLOR, r, sr + sg + sb, location);
		return r;
	}

	public String toString()
	{
		return "constant " + value;
	}
}
