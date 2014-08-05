/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class KeywordArgument
{
	protected String name;
	protected AST arg;

	public KeywordArgument(String name, AST arg)
	{
		this.name = name;
		this.arg = arg;
	}

	public String toString()
	{
		return name + "=" + arg.toString();
	}

	public String getName()
	{
		return name;
	}

	public AST getArg()
	{
		return arg;
	}
}
