/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;

public class For extends AST
{
	protected String itername;
	protected AST container;

	public For(String itername, AST container)
	{
		this.itername = itername;
		this.container = container;
	}

	public String name()
	{
		return "for";
	}

	public String toString()
	{
		return "for(" + Utils.repr(itername) + ", " + container + ")";
	}
}
