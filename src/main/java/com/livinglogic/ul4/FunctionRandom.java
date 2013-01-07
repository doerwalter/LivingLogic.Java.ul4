/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Random;

public class FunctionRandom extends NormalFunction
{
	public String getName()
	{
		return "random";
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call();
	}

	private static Random rng = new Random();

	public static double call()
	{
		return rng.nextDouble();
	}
}
