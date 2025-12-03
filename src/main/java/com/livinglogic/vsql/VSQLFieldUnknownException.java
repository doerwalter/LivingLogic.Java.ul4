/*
** Copyright 2015-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

/**
An exception that is thrown when a field is unknown.
**/
public class VSQLFieldUnknownException extends RuntimeException
{
	public VSQLFieldUnknownException(String message)
	{
		super(message);
	}
}
