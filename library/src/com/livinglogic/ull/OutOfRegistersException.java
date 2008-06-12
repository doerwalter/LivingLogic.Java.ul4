package com.livinglogic.ull;

public class OutOfRegistersException extends RuntimeException
{
	public OutOfRegistersException()
	{
		super("out of registers");
	}
}
