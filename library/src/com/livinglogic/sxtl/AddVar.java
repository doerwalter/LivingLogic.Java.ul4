package com.livinglogic.sxtl;

public class AddVar extends ChangeVar
{
	public AddVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public String getOpcode()
	{
		return "addvar";
	}
}
