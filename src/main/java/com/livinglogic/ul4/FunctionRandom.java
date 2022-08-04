/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Random;

public class FunctionRandom extends Function
{
	@Override
	public String getNameUL4()
	{
		return "random";
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call();
	}

	private static Random rng = new Random();

	public static double call()
	{
		return rng.nextDouble();
	}

	public static final Function function = new FunctionRandom();
}
