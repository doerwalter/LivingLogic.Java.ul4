package com.livinglogic.ull;

public class SyntaxException extends Exception
{
	public SyntaxException(Token token)
	{
		super("Lexical error near " + token);
	}
}
