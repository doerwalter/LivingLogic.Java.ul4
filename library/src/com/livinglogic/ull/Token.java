package com.livinglogic.ull;

public class Token
{
	protected int start;
	protected int end;
	protected String type;

	public Token(int start, int end, String type)
	{
		this.start = start;
		this.end = end;
		this.type = type;
	}

	public int getStart()
	{
		return start;
	}

	public int getEnd()
	{
		return end;
	}

	public String getTokenType()
	{
		return type;
	}

	public String toString()
	{
		return "token \"" + type.replace("\"", "\\\"") + "\"";
	}
}
