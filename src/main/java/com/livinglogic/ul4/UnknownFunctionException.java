/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Thrown when an unknown function is encountered in an UL4 template.
 */
public class UnknownFunctionException extends RuntimeException
{
	public UnknownFunctionException(String functionName)
	{
		super("Function '" + functionName + "' unknown!");
	}
}
