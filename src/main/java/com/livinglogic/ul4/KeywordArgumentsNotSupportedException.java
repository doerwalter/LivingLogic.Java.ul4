/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Exception when a function is called with keyword arguments, but the function doesn't support any
 */
public class KeywordArgumentsNotSupportedException extends ArgumentException
{
	public KeywordArgumentsNotSupportedException(String name)
	{
		super(name + "() doesn't support keyword arguments");
	}

	public KeywordArgumentsNotSupportedException(Signature signature)
	{
		this(signature.getName());
	}
}
