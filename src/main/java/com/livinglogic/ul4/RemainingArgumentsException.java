/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Exception when a * argument isn't a list
 */
public class RemainingArgumentsException extends ArgumentException
{
	public RemainingArgumentsException(String name)
	{
		super("* argument for " + name + "() must be list");
	}

	public RemainingArgumentsException(ArgumentDescriptions descriptions)
	{
		this(descriptions.getName());
	}
}
