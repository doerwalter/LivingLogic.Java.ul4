package com.livinglogic.sxtl;

public class StoreVar extends ChangeVar
{
	public StoreVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public String getOpcode()
	{
		return "storevar";
	}
}
