package com.livinglogic.ul4;

public class OutOfRegistersException extends RuntimeException
{
	public OutOfRegistersException()
	{
		super("out of registers");
	}
}
