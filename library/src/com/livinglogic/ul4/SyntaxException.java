package com.livinglogic.ul4;

public class SyntaxException extends Exception
{
	public SyntaxException(Token token)
	{
		super("Lexical error near " + token);
	}
}
