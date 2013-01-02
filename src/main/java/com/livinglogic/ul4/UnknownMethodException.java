/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Thrown when an unknown method is encountered in an UL4 template.
 */
public class UnknownMethodException extends RuntimeException
{
	public UnknownMethodException(String methodName)
	{
		super("Method '" + methodName + "' unknown!");
	}
}
