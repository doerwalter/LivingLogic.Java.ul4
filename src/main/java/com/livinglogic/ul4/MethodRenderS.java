/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodRenderS implements Method
{
	public String getName()
	{
		return "renders";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(context, obj);
			default:
				throw new ArgumentCountMismatchException("method", "renders", args.length, 0);
		}
	}

	public static String call(EvaluationContext context, Template obj)
	{
		return obj.renders(context, null);
	}

	public static String call(EvaluationContext context, Object obj)
	{
		if (obj instanceof Template)
			return call(context, (Template)obj);
		throw new UnsupportedOperationException("renders() method requires a template!");
	}
}
