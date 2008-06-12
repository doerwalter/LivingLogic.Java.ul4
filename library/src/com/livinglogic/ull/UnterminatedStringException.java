package com.livinglogic.ull;

public class UnterminatedStringException extends Exception
{
	public UnterminatedStringException()
	{
		super("Unterminated string");
	}
}
