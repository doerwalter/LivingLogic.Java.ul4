/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
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
		super(name + "() argument " + FunctionRepr.call(argumentName) + (argumentPosition >= 0 ? " (position " + argumentPosition + ")": "") + " specified multiple times");
	}

	public DuplicateArgumentException(String name, String argumentName)
	{
		this(name, argumentName, -1);
	}

	public DuplicateArgumentException(UL4Name object, String argumentName)
	{
		this(object.nameUL4(), argumentName);
	}

	public DuplicateArgumentException(Object object, String argumentName)
	{
		this(object instanceof UL4Name ? ((UL4Name)object).nameUL4() : Utils.objectType(object), argumentName);
	}

	public DuplicateArgumentException(String name, ArgumentDescription argument)
	{
		this(name, argument.getName(), argument.getPosition());
	}

	public DuplicateArgumentException(UL4Name object, ArgumentDescription argument)
	{
		this(object.nameUL4(), argument);
	}
}
