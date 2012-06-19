/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionFromUL4ON implements Function
{
	public String getName()
	{
		return "fromul4on";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "fromul4on", args.length, 1);
	}

	public static Object call(String obj)
	{
		return com.livinglogic.ul4on.Utils.loads(obj);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new UnsupportedOperationException("fromul4on(" + Utils.objectType(obj) + ") not supported!");
	}
}
