/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class UndefinedKey extends Undefined
{
	private Object key;

	public UndefinedKey(Object key)
	{
		this.key = key;
	}

	public String toString()
	{
		return "UndefinedKey(" + FunctionRepr.call(key) + ")";
	}
}
