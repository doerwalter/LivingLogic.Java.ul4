/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Exception when a ** argument isn't a dict
 */
public class RemainingKeywordArgumentsException extends ArgumentException
{
	public RemainingKeywordArgumentsException(String name)
	{
		super("** argument for " + name + "() must be dict with string keys");
	}

	public RemainingKeywordArgumentsException(ArgumentDescriptions descriptions)
	{
		this(descriptions.getName());
	}
}
