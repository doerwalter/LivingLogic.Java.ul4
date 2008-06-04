package com.livinglogic.ull;

abstract class AST
{
	protected int start;
	protected int end;

	public AST(int start, int end)
	{
		this.start = start;
		this.end = end;
	}

	public int getStart()
	{
		return start;
	}

	public int getEnd()
	{
		return end;
	}

	abstract public int compile(Template template, Registers registers, Location location);
}