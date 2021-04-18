/*
** Copyright 2015-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class EnumValueException extends RuntimeException
{
	public EnumValueException(String enumName, String value)
	{
		super("Value " + FunctionRepr.call(value) + " not supported for " + enumName);
	}
}
