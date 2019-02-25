/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" ");
		formatter.visit(key);
		formatter.append(">");
	}

	public String toString()
	{
		return "UndefinedKey(" + FunctionRepr.call(key) + ")";
	}
}
