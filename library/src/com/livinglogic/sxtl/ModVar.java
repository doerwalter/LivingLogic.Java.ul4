package com.livinglogic.sxtl;

public class ModVar extends ChangeVar
{
	public ModVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public String getType()
	{
		return "modvar";
	}
}
