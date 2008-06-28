package com.livinglogic.ul4;

public class UnknownMethodException extends RuntimeException
{
	public UnknownMethodException(String methodName)
	{
		super("No method '" + methodName + "' defined!");
	}
}
