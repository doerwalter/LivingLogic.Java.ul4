package com.livinglogic.sxtl;

public class None extends Const
{
	public None(int start, int end)
	{
		super(start, end);
	}

	public String getOpcode()
	{
		return "loadnone";
	}
}