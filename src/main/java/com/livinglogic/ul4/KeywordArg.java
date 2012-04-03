/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class KeywordArg
{
	protected String name;
	protected AST value;

	public KeywordArg(String name, AST value)
	{
		this.name = name;
		this.value = value;
	}

	public KeywordArg(AST value)
	{
		this.name = null;
		this.value = value;
	}
}
