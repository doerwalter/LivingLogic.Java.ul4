/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
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

	public MissingArgumentException(Signature signature, ArgumentDescription argument)
	{
		this(signature.getName(), argument.getName(), argument.getPosition());
	}
}
