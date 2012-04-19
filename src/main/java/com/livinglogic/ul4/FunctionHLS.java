/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionHLS implements Function
{
	public Object call(EvaluationContext context, Object... args)
	{
		if (args.length == 3)
			return Utils.hls(args[0], args[1], args[2]);
		else if (args.length == 4)
			return Utils.hls(args[0], args[1], args[2], args[3]);
		throw new ArgumentCountMismatchException("function", "hls", args.length, 3, 4);
	}

	public String getName()
	{
		return "hls";
	}
}
