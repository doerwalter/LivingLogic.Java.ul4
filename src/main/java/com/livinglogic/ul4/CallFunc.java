/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;

public class CallFunc extends AST
{
	protected String name;
	protected LinkedList<AST> args;

	public CallFunc(String name)
	{
		this.name = name;
		this.args = new LinkedList<AST>();
	}

	public void append(AST arg)
	{
		args.add(arg);
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("callfunc(");
		buffer.append(Utils.repr(name));
		for (AST arg : args)
		{
			buffer.append(", ");
			buffer.append(arg);
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String name()
	{
		return "callfunc";
	}
}
