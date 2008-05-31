package com.livinglogic.sxtl;

public class SubVar extends ChangeVar
{
	public SubVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public String getType()
	{
		return "subvar";
	}
}
