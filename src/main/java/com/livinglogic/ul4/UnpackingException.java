/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Thrown in a for loop when the loop variables are unpacked and the number of
items from the iterator doesn't match the number of loop variables.
**/
public class UnpackingException extends RuntimeException
{
	public UnpackingException(String message)
	{
		super(message);
	}
}
