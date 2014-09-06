/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Exception when a required argument to a function is missing
 */
public class MissingArgumentException extends ArgumentException
{
	public MissingArgumentException(String name, String argumentName, int argumentPosition)
	{
		super("required " + name + "() argument " + FunctionRepr.call(argumentName) + " (position " + argumentPosition + ") missing");
	}

	public MissingArgumentException(UL4Name object, String argumentName, int argumentPosition)
	{
		this(object.nameUL4(), argumentName, argumentPosition);
	}

	public MissingArgumentException(String name, ArgumentDescription argument)
	{
		this(name, argument.getName(), argument.getPosition());
	}

	public MissingArgumentException(UL4Name name, ArgumentDescription argument)
	{
		this(name.nameUL4(), argument);
	}
}
