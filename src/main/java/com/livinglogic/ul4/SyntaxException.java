package com.livinglogic.ul4;

public class SyntaxException extends Exception
{
	public SyntaxException(Token token)
	{
		super("Lexical error near " + token);
	}

	public SyntaxException(Object object)
	{
		super("Lexical error near " + object);
	}
}
