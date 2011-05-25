package com.livinglogic.ul4;

public abstract class AST
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

	public void setEnd(int end)
	{
		this.end = end;
	}

	abstract public int compile(InterpretedTemplate template, Registers registers, Location location);
}
