package com.livinglogic.ul4;

public class UnknownFunctionException extends RuntimeException
{
	public UnknownFunctionException(String functionName)
	{
		super("Function '" + functionName + "' unknown!");
	}
}
