/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionFromUL4ON implements Function
{
	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
		{
			if (!(args[0] instanceof String))
				throw new UnsupportedOperationException("fromul4on(" + Utils.objectType(args[0]) + ") not supported!");
			return com.livinglogic.ul4on.Utils.loads((String)args[0]);
		}
		throw new ArgumentCountMismatchException("function", "fromul4on", args.length, 1);
	}

	public String getName()
	{
		return "fromul4on";
	}
}
