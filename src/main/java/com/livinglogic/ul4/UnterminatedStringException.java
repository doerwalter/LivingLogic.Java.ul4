package com.livinglogic.ul4;

public class UnterminatedStringException extends Exception
{
	public UnterminatedStringException()
	{
		super("Unterminated string");
	}
}
