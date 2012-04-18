/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.HashMap;
import java.io.IOException;

public class CallMethKeywords extends AST
{
	protected AST obj;
	protected String name;
	protected LinkedList<CallArg> args = new LinkedList<CallArg>();

	public CallMethKeywords(AST obj, String name)
	{
		this.obj = obj;
		this.name = name;
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

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("callmethkw(");
		buffer.append(obj.toString(indent));
		buffer.append(", ");
		buffer.append(Utils.repr(name));
		for (CallArg arg : args)
		{
			buffer.append(", ");
			buffer.append(arg);
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String getType()
	{
		return "callmethkw";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object obj = this.obj.evaluate(context);

		// make argument dictionary
		HashMap<String, Object> args = new HashMap<String, Object>();

		for (CallArg arg : this.args)
			arg.addTo(context, args);

		if (name.equals("render")) // FIXME: Use switch in Java 7
		{
			if (null != obj && obj instanceof InterpretedTemplate)
			{
				((InterpretedTemplate)obj).render(context.getWriter(), args);
				return null;
			}
			throw new UnsupportedOperationException("render() method requires a template!");
		}
		else if (name.equals("renders"))
		{
			if (null != obj && obj instanceof InterpretedTemplate)
			{
				return ((InterpretedTemplate)obj).renders(args);
			}
			throw new UnsupportedOperationException("render() method requires a template!");
		}
		else
		{
				throw new UnknownMethodException(name);
		}
	}
}
