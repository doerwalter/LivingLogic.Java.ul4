/*
** Copyright 2015-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


/**
Thrown when an invalid enum value is encountered.
**/
public class EnumValueException extends RuntimeException
{
	public EnumValueException(String enumName, String value)
	{
		super("Value " + FunctionRepr.call(value) + " not supported for " + enumName);
	}
}
