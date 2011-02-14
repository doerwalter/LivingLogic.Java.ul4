package com.livinglogic.ul4;

public class UnknownMethodException extends RuntimeException
{
	public UnknownMethodException(String methodName)
	{
		super("Method '" + methodName + "' unknown!");
	}
}
