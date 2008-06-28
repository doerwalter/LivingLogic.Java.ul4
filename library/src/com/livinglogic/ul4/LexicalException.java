package com.livinglogic.ul4;

public class LexicalException extends Exception
{
	public LexicalException(int start, int end, String string)
	{
		super("Unmatched input " + string);
	}
}
