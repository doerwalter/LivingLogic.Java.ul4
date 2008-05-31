package com.livinglogic.sxtl;

abstract class AST
{
	public int start;
	public int end;

	public AST(int start, int end)
	{
		this.start = start;
		this.end = end;
	}

	abstract public String getType();

	abstract public int compile(Template template, Registers registers, Template.Location location);
}