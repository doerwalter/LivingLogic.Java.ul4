/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Exception when to many positional arguments are passed
**/
public class TooManyArgumentsException extends ArgumentException
{
	public TooManyArgumentsException(String name, int expected, int given)
	{
		super(name + "() expects at most " + expected + " positional argument" + (expected != 1 ? "s" : "") + ", " + given + " given");
	}

	public TooManyArgumentsException(UL4Name object, Signature signature, int given)
	{
		this(object.getFullNameUL4(), signature.countPositionalOnly() + signature.countPositionalOrKeyword(), given);
	}
}
