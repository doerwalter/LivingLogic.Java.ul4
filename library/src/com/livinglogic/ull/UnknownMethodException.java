package com.livinglogic.ull;

public class UnknownMethodException extends RuntimeException
{
	public UnknownMethodException(String methodName)
	{
		super("No method '" + methodName + "' defined!");
	}
}
