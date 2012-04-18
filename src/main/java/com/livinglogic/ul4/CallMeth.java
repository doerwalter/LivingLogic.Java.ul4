/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Map;
import java.io.IOException;

public class CallMeth extends AST
{
	protected AST obj;
	protected String name;
	protected LinkedList<AST> args;

	public CallMeth(AST obj, String name)
	{
		this.obj = obj;
		this.name = name;
		this.args = new LinkedList<AST>();
	}

	public void append(AST arg)
	{
		args.add(arg);
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("callmeth(");
		buffer.append(obj);
		buffer.append(", ");
		buffer.append(Utils.repr(name));
		for (AST arg : args)
		{
			buffer.append(", ");
			buffer.append(arg);
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String getType()
	{
		return "callmeth";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object obj = this.obj.evaluate(context);

		int argcount = args.size();

		if (name.equals("split"))
		{
			if (argcount == 0)
				return Utils.split(obj);
			else if (argcount == 1)
				return Utils.split(obj, args.get(0).evaluate(context));
			else if (argcount == 2)
				return Utils.split(obj, args.get(0).evaluate(context), args.get(1).evaluate(context));
			throw new ArgumentCountMismatchException("method", "split", argcount, 0, 3);
		}
		else if (name.equals("rsplit"))
		{
			if (argcount == 0)
				return Utils.rsplit(obj);
			else if (argcount == 1)
				return Utils.rsplit(obj, args.get(0).evaluate(context));
			else if (argcount == 2)
				return Utils.rsplit(obj, args.get(0).evaluate(context), args.get(1).evaluate(context));
			throw new ArgumentCountMismatchException("method", "rsplit", argcount, 0, 3);
		}
		else if (name.equals("strip"))
		{
			if (argcount == 0)
				return Utils.strip(obj);
			else if (argcount == 1)
				return Utils.strip(obj, args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("method", "strip", argcount, 0, 1);
		}
		else if (name.equals("lstrip"))
		{
			if (argcount == 0)
				return Utils.lstrip(obj);
			else if (argcount == 1)
				return Utils.lstrip(obj, args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("method", "lstrip", argcount, 0, 1);
		}
		else if (name.equals("rstrip"))
		{
			if (argcount == 0)
				return Utils.rstrip(obj);
			else if (argcount == 1)
				return Utils.rstrip(obj, args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("method", "rstrip", argcount, 0, 1);
		}
		else if (name.equals("upper"))
		{
			if (argcount == 0)
				return Utils.upper(obj);
			throw new ArgumentCountMismatchException("method", "upper", argcount, 0);
		}
		else if (name.equals("lower"))
		{
			if (argcount == 0)
				return Utils.lower(obj);
			throw new ArgumentCountMismatchException("method", "lower", argcount, 0);
		}
		else if (name.equals("capitalize"))
		{
			if (argcount == 0)
				return Utils.capitalize(obj);
			throw new ArgumentCountMismatchException("method", "capitalize", argcount, 0);
		}
		else if (name.equals("items"))
		{
			if (argcount == 0)
				return Utils.items(obj);
			throw new ArgumentCountMismatchException("method", "items", argcount, 0);
		}
		else if (name.equals("isoformat"))
		{
			if (argcount == 0)
				return Utils.isoformat(obj);
			throw new ArgumentCountMismatchException("method", "isoformat", argcount, 0);
		}
		else if (name.equals("mimeformat"))
		{
			if (argcount == 0)
				return Utils.mimeformat(obj);
			throw new ArgumentCountMismatchException("method", "mimeformat", argcount, 0);
		}
		else if (name.equals("r"))
		{
			if (argcount == 0)
				return ((Color)obj).getR();
			throw new ArgumentCountMismatchException("method", "r", argcount, 0);
		}
		else if (name.equals("g"))
		{
			if (argcount == 0)
				return ((Color)obj).getG();
			throw new ArgumentCountMismatchException("method", "g", argcount, 0);
		}
		else if (name.equals("b"))
		{
			if (argcount == 0)
				return ((Color)obj).getB();
			throw new ArgumentCountMismatchException("method", "b", argcount, 0);
		}
		else if (name.equals("a"))
		{
			if (argcount == 0)
				return ((Color)obj).getA();
			throw new ArgumentCountMismatchException("method", "a", argcount, 0);
		}
		else if (name.equals("hls"))
		{
			if (argcount == 0)
				return ((Color)obj).hls();
			throw new ArgumentCountMismatchException("method", "hls", argcount, 0);
		}
		else if (name.equals("hlsa"))
		{
			if (argcount == 0)
				return ((Color)obj).hlsa();
			throw new ArgumentCountMismatchException("method", "hlsa", argcount, 0);
		}
		else if (name.equals("hsv"))
		{
			if (argcount == 0)
				return ((Color)obj).hsv();
			throw new ArgumentCountMismatchException("method", "hsv", argcount, 0);
		}
		else if (name.equals("hsva"))
		{
			if (argcount == 0)
				return ((Color)obj).hsva();
			throw new ArgumentCountMismatchException("method", "hsva", argcount, 0);
		}
		else if (name.equals("lum"))
		{
			if (argcount == 0)
				return ((Color)obj).lum();
			throw new ArgumentCountMismatchException("method", "lum", argcount, 0);
		}
		else if (name.equals("day"))
		{
			if (argcount == 0)
				return Utils.day(obj);
			throw new ArgumentCountMismatchException("method", "day", argcount, 0);
		}
		else if (name.equals("month"))
		{
			if (argcount == 0)
				return Utils.month(obj);
			throw new ArgumentCountMismatchException("method", "month", argcount, 0);
		}
		else if (name.equals("year"))
		{
			if (argcount == 0)
				return Utils.year(obj);
			throw new ArgumentCountMismatchException("method", "year", argcount, 0);
		}
		else if (name.equals("hour"))
		{
			if (argcount == 0)
				return Utils.hour(obj);
			throw new ArgumentCountMismatchException("method", "hour", argcount, 0);
		}
		else if (name.equals("minute"))
		{
			if (argcount == 0)
				return Utils.minute(obj);
			throw new ArgumentCountMismatchException("method", "minute", argcount, 0);
		}
		else if (name.equals("second"))
		{
			if (argcount == 0)
				return Utils.second(obj);
			throw new ArgumentCountMismatchException("method", "second", argcount, 0);
		}
		else if (name.equals("microsecond"))
		{
			if (argcount == 0)
				return Utils.microsecond(obj);
			throw new ArgumentCountMismatchException("method", "microsecond", argcount, 0);
		}
		else if (name.equals("weekday"))
		{
			if (argcount == 0)
				return Utils.weekday(obj);
			throw new ArgumentCountMismatchException("method", "weekday", argcount, 0);
		}
		else if (name.equals("yearday"))
		{
			if (argcount == 0)
				return Utils.yearday(obj);
			throw new ArgumentCountMismatchException("method", "yearday", argcount, 0);
		}
		else if (name.equals("render"))
		{
			if (argcount == 0)
			{
				((InterpretedTemplate)obj).render(context.getWriter(), null);
				return null;
			}
			throw new ArgumentCountMismatchException("method", "render", argcount, 0);
		}
		else if (name.equals("renders"))
		{
			if (argcount == 0)
				return ((InterpretedTemplate)obj).renders(null);
			throw new ArgumentCountMismatchException("method", "render", argcount, 0);
		}
		else if (name.equals("startswith"))
		{
			if (argcount == 1)
				return Utils.startswith(obj, args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("method", "startswith", argcount, 1);
		}
		else if (name.equals("endswith"))
		{
			if (argcount == 1)
				return Utils.endswith(obj, args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("method", "endswith", argcount, 1);
		}
		else if (name.equals("find"))
		{
			if (argcount == 1)
				return Utils.find(obj, args.get(0).evaluate(context));
			else if (argcount == 2)
				return Utils.find(obj, args.get(0).evaluate(context), args.get(1).evaluate(context));
			else if (argcount == 3)
				return Utils.find(obj, args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(2).evaluate(context));
			throw new ArgumentCountMismatchException("method", "find", argcount, 1, 3);
		}
		else if (name.equals("rfind"))
		{
			if (argcount == 1)
				return Utils.rfind(obj, args.get(0).evaluate(context));
			else if (argcount == 2)
				return Utils.rfind(obj, args.get(0).evaluate(context), args.get(1).evaluate(context));
			else if (argcount == 3)
				return Utils.rfind(obj, args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(2).evaluate(context));
			throw new ArgumentCountMismatchException("method", "rfind", argcount, 1, 3);
		}
		else if (name.equals("get"))
		{
			if (argcount == 1)
				return ((Map)obj).get(args.get(0).evaluate(context));
			else if (argcount == 2)
			{
				Object arg0 = args.get(0).evaluate(context);
				if (((Map)obj).containsKey(arg0))
					return arg0;
				return args.get(1).evaluate(context);
			}
			throw new ArgumentCountMismatchException("method", "get", argcount, 1, 2);
		}
		else if (name.equals("withlum"))
		{
			if (argcount == 1)
				return Utils.withlum(obj, args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("method", "withlum", argcount, 1);
		}
		else if (name.equals("witha"))
		{
			if (argcount == 1)
				return Utils.witha(obj, args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("method", "witha", argcount, 1);
		}
		else if (name.equals("join"))
		{
			if (argcount == 1)
				return Utils.join(obj, args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("method", "join", argcount, 1);
		}
		else if (name.equals("replace"))
		{
			if (argcount == 2)
				return Utils.replace(obj, args.get(0).evaluate(context), args.get(1).evaluate(context));
			throw new ArgumentCountMismatchException("method", "replace", argcount, 2);
		}
		else
		{
			throw new UnknownMethodException(name);
		}
	}
}
