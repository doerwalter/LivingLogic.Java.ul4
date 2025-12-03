/*
** Copyright 2015-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

/**
An exception that is thrown when a VSQL operation is not supported.

This normally means that the operation exists, but the type combination
of argument is not supported.
**/
public class VSQLUnsupportedOperationException extends RuntimeException
{
	public VSQLUnsupportedOperationException(String message)
	{
		super(message);
	}
}
