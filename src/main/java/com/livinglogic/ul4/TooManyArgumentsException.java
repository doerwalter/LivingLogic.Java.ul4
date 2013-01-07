/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Exception when to many positional arguments are passed
 */
public class TooManyArgumentsException extends ArgumentException
{
	public TooManyArgumentsException(String name, int expected, int given)
	{
		super(name + "() expects at most " + expected + " positional argument" + (expected != 1 ? "s" : "") + ", " + given + " given");
	}

	public TooManyArgumentsException(ArgumentDescriptions argumentDescriptions, int given)
	{
		this(argumentDescriptions.getName(), argumentDescriptions.size(), given);
	}
}
