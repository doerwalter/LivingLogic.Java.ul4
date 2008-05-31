package com.livinglogic.sxtl;

class AST
{
	public int start;
	public int end;

	public AST(int start, int end)
	{
		this.start = start;
		this.end = end;
	}

	public int compile(Template template, Registers registers, Template.Location location)
	{
		return -1;
	}
}