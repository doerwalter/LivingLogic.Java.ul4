/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class DictItem
{
	protected AST key;
	protected AST value;
	protected boolean isdict; // is this a real (key, value) paar or a **dict argument?

	public DictItem(AST key, AST value)
	{
		this.key = key;
		this.value = value;
		isdict = false;
	}

	public DictItem(AST value)
	{
		this.key = null;
		this.value = value;
		isdict = true;
	}
}
