/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Random;

public class FunctionRandom implements Function
{
	public String getName()
	{
		return "random";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return call();
		throw new ArgumentCountMismatchException("function", "random", args.length, 0);
	}

	private static Random rng = new Random();

	public static double call()
	{
		return rng.nextDouble();
	}
}
