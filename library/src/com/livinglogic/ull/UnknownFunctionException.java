package com.livinglogic.ull;

public class UnknownFunctionException extends RuntimeException
{
	public UnknownFunctionException(String functionName)
	{
		super("No function '" + functionName + "' defined!");
	}
}
