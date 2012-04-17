/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;

public class Render extends AST
{
	protected AST template;
	protected LinkedList<CallArg> args = new LinkedList<CallArg>();

	public Render(AST template)
	{
		this.template = template;
	}

	public void append(String name, AST value)
	{
		args.add(new CallArgNamed(name, value));
	}

	public void append(AST value)
	{
		args.add(new CallArgDict(value));
	}

	public void append(CallArg arg)
	{
		args.add(arg);
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("render(");
		buffer.append(template);
		for (CallArg arg : args)
		{
			buffer.append(", ");
			buffer.append(arg);
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String name()
	{
		return "render";
	}
}
