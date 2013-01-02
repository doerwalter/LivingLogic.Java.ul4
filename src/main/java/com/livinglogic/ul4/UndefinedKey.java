/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
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
		return "undefined object for key " + FunctionRepr.call(key);
	}
}
