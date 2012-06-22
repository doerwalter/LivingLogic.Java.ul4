/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Random;
import java.util.List;

public class FunctionRandChoice implements Function
{
	public String getName()
	{
		return "randchoice";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "randchoice", args.length, 1);
	}

	private static Random rng = new Random();

	public static String call(String obj)
	{
		int index = (int)(obj.length() * rng.nextDouble());
		return obj.substring(index, index + 1);
	}

	public static Object call(List obj)
	{
		int index = (int)(obj.size() * rng.nextDouble());
		return obj.get(index);
	}

	public static int call(Color obj)
	{
		int index = (int)(4 * rng.nextDouble());
		switch (index)
		{
			case 0:
				return obj.getR();
			case 1:
				return obj.getG();
			case 2:
				return obj.getB();
			case 3:
				return obj.getA();
			default:
				return 0; // can't happen
		}
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		else if (obj instanceof List)
			return call((List)obj);
		else if (obj instanceof Color)
			return call((Color)obj);
		throw new ArgumentTypeMismatchException("randchoice({})", obj);
	}
}
