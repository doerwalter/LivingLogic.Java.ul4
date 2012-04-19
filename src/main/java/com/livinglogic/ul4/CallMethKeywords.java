/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class CallMethKeywords extends AST
{
	protected AST obj;
	protected String methname;
	protected LinkedList<CallArg> args = new LinkedList<CallArg>();

	public CallMethKeywords(AST obj, String methname)
	{
		this.obj = obj;
		this.methname = methname;
	}

	public void append(String methname, AST value)
	{
		args.add(new CallArgNamed(methname, value));
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
		buffer.append(Utils.repr(methname));
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
		if (methname.equals("render")) // FIXME: Use switch in Java 7
		{
			if (null != obj && obj instanceof Template)
			{
				((Template)obj).render(context.getWriter(), args);
				return null;
			}
			throw new UnsupportedOperationException("render() method requires a template!");
		}
		else if (methname.equals("renders"))
		{
			if (null != obj && obj instanceof Template)
			{
				return ((Template)obj).renders(args);
			}
			throw new UnsupportedOperationException("render() method requires a template!");
		}
		else
		{
				throw new UnknownMethodException(methname);
		}
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("obj", new ValueMaker(){public Object getValue(Object object){return ((CallMethKeywords)object).obj;}});
			v.put("methname", new ValueMaker(){public Object getValue(Object object){return ((CallMethKeywords)object).methname;}});
			v.put("args", new ValueMaker(){public Object getValue(Object object){return ((CallMethKeywords)object).args;}});
			valueMakers = v;
		}
		return valueMakers;
	}}
