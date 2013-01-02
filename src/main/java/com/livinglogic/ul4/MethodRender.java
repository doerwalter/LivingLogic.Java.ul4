/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.Writer;

public class MethodRender implements Method
{
	public String getName()
	{
		return "render";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				call(context.getWriter(), obj);
				return null;
			default:
				throw new ArgumentCountMismatchException("method", "render", args.length, 0);
		}
	}

	public static void call(Writer writer, Template obj)
	{
		try
		{
			obj.render(writer, null);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static void call(Writer writer, Object obj)
	{
		if (obj instanceof Template)
			call(writer, (Template)obj);
		else
			throw new UnsupportedOperationException("render() method requires a template!");
	}
}
