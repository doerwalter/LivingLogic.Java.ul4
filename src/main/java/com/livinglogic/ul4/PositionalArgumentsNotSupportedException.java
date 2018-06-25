/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Exception when a function is called with positional arguments, but the function doesn't support any
 */
public class PositionalArgumentsNotSupportedException extends ArgumentException
{
	public PositionalArgumentsNotSupportedException(String name)
	{
		super(name + "() doesn't support positional arguments");
	}

	public PositionalArgumentsNotSupportedException(UL4Name object)
	{
		this(object.nameUL4());
	}
}
