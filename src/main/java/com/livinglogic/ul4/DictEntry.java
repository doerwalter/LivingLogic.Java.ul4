/*
** Copyright 2009-2011 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class DictEntry
{
	protected AST key;
	protected AST value;
	protected boolean isdict;

	public DictEntry(AST key, AST value)
	{
		this.key = key;
		this.value = value;
		isdict = false;
	}

	public DictEntry(AST value)
	{
		this.key = null;
		this.value = value;
		isdict = true;
	}
}
