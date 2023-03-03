/*
** Copyright 2015-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import com.livinglogic.ul4.FunctionRepr;

public class EnumValueException extends RuntimeException
{
	public EnumValueException(String enumName, String value)
	{
		super("Value " + FunctionRepr.call(value) + " not supported from " + enumName);
	}
}
