package com.livinglogic.ul4;

public class KeyException extends RuntimeException
{
	public KeyException(Object key)
	{
		super("Key '" + key + "' not found!");
	}
}
