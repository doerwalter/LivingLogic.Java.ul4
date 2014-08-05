/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
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

	public TooManyArgumentsException(Signature signature, int given)
	{
		this(signature.getName(), signature.size(), given);
	}
}
