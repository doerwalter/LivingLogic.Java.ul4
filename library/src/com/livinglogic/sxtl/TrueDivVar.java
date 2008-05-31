package com.livinglogic.sxtl;

public class TrueDivVar extends ChangeVar
{
	public TrueDivVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public String getOpcode()
	{
		return "truedivvar";
	}
}
