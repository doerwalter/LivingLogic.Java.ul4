/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Exception when an argument has been specified both as a positional argument and as a keyword argument.
 */
public class DuplicateArgumentException extends ArgumentException
{
	public DuplicateArgumentException(String name, String argumentName, int argumentPosition)
	{
		super(name + "() argument given by name (" + FunctionRepr.call(argumentName) + ") and position (" + argumentPosition + ")");
	}

	public DuplicateArgumentException(ArgumentDescriptions descriptions, ArgumentDescription description)
	{
		this(descriptions.getName(), description.getName(), description.getPosition());
	}
}
