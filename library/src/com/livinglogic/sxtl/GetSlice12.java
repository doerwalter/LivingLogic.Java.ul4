package com.livinglogic.sxtl;

public class GetSlice12 extends AST
{
	protected AST obj;
	protected AST index1;
	protected AST index2;

	public GetSlice12(int start, int end, AST obj, AST index1, AST index2)
	{
		super(start, end);
		this.obj = obj;
		this.index1 = index1;
		this.index2 = index2;
	}

	public String getType()
	{
		return "getslice12";
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = obj.compile(template, registers, location);
		int r1 = index1.compile(template, registers, location);
		int r2 = index2.compile(template, registers, location);
		template.opcode("getslice12", r, r, r1, r2, location);
		return r;
	}
}
