/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;

public class ForUnpack extends AST
{
	protected LinkedList<String> iternames;
	protected AST container;

	public ForUnpack(AST container)
	{
		this.iternames = new LinkedList<String>();
		this.container = container;
	}

	public ForUnpack()
	{
		this.iternames = new LinkedList<String>();
		this.container = null;
	}

	public void append(String itername)
	{
		iternames.add(itername);
	}

	public void setContainer(AST container)
	{
		this.container = container;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("foru(");
		boolean first = true;
		for (CallArg itername : iternames)
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(Utils.repr(itername));
		}
		buffer.append(", ");
		buffer.append(container);
		buffer.append(")");
		return buffer.toString();
	}

	public String name()
	{
		return "foru";
	}
}
