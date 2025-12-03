/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Exception when an argument has been by keyword that is not supported
**/
public class UnsupportedArgumentNameException extends ArgumentException
{
	public UnsupportedArgumentNameException(String name, String argumentName)
	{
		super(name + "() doesn't support an argument named " + FunctionRepr.call(argumentName));
	}

	public UnsupportedArgumentNameException(UL4Name object, String argumentName)
	{
		this(object.getFullNameUL4(), argumentName);
	}
}
