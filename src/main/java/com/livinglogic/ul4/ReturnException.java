/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ReturnException extends RuntimeException
{
	private Object value;

	public ReturnException(Object value)
	{
		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}
}
