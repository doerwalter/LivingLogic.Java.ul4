package com.livinglogic.sxtl;

public class True extends Const
{
	public True(int start, int end)
	{
		super(start, end);
	}

	public String getOpcode()
	{
		return "loadtrue";
	}
}